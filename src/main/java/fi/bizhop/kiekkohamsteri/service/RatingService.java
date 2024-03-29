package fi.bizhop.kiekkohamsteri.service;

import java.io.IOException;
import java.util.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.dto.v1.RatingDto;
import fi.bizhop.kiekkohamsteri.dto.v1.RoundDto;

@Service
public class RatingService {
    private static final Logger LOG = LogManager.getLogger(RatingService.class);

    private static final String RATINGS_URL = "https://www.pdga.com/player/%s/details";
    private static final String PLAYER_URL = "https://www.pdga.com/player/%s";

    public RatingDto getRounds(String pdga_num) throws Exception {
        List<RoundDto> rounds = new ArrayList<>();
        Set<String> events = new HashSet<>();

        //Viralliset kierrokset (RATINGS DETAIL)
        try {
            Document doc = Jsoup.connect(String.format(RATINGS_URL, pdga_num)).get();
            Elements rows = doc.getElementById("player-results-details").select("tbody").select("tr");
            for (Element row : rows) {
                Elements tds = row.select("td");
                if (tds.size() == 8) {
                    String link = getLink(tds.get(0));
                    RoundDto round = new RoundDto(
                            getText(tds.get(0)),
                            link,
                            getDate(tds.get(2)),
                            getInt(tds.get(3)),
                            getInt(tds.get(4)),
                            getInt(tds.get(5)),
                            getHoles(link),
                            getBoolean(tds.get(7)),
                            false);
                    events.add(getEventNumber(link));
                    rounds.add(round);
                }
            }
        } catch (Exception e) {
            LOG.error("Virallisten kierrosten haku epäonnistui", e);
            throw new Exception("Internal error");
        }

        //Epäviralliset kierrokset
        try {
            Document info = Jsoup.connect(String.format(PLAYER_URL, pdga_num)).get();
            //Pelaajan etusivulta
            try {
                Elements links = info.select("table[id]").select("a");
                for(Element l : links) {
                    String link = l.attr("abs:href");
                    //Lisätään tästä vain jos tapahtumaa ei lisätty jo Ratings Detail -sivulta
                    if(!events.contains(getEventNumber(link))) {
                        rounds.addAll(getUnofficial(link, pdga_num));
                    }
                }
            } catch (Exception e) {
                LOG.error("Etusivun kierrosten haku epäonnistui", e);
            }
            //Viimeisimmät tapahtumat (recent events)
            try {
                Element recent = info.getElementsByClass("recent-events").first();
                if (recent != null) {
                    Elements links = recent.select("a");
                    for (Element l : links) {
                        rounds.addAll(getUnofficial(l.attr("abs:href"), pdga_num));
                    }
                }
            } catch (Exception e) {
                LOG.error("Viimeisten kierrosten haku epäonnistui", e);
            }
        } catch (Exception e) {
            LOG.error("Epävirallisten kierrosten haku epäonnistui", e);
        }

        LOG.debug(String.format("Rating for PDGA #%s", pdga_num));
        return getRating(rounds, true, false);
    }

    public RatingDto getRating(List<RoundDto> rounds, boolean calculateDoubles, boolean byRoundsOnly) {
        int nextRating = calculateNextRating(rounds, calculateDoubles, byRoundsOnly);

        return new RatingDto(rounds, nextRating);
    }

    private static List<RoundDto> getUnofficial(String url, String pdga) throws IOException {
        List<RoundDto> rounds = new ArrayList<>();
        Document uo = Jsoup.connect(url).get();
        String title = uo.getElementsByClass("pane-page-title").first().select("h1").first().text().trim();
        String date = getDate(uo.getElementsByClass("tournament-date").first());
        Elements rows = uo.select("tr");
        for (Element row : rows) {
            Element searchPdga = row.getElementsByClass("pdga-number").first();
            if (searchPdga != null && pdga.equals(searchPdga.text().trim())) {
                Elements roundElements = row.getElementsByClass("round");
                Elements ratingElements = row.getElementsByClass("round-rating");
                for (int i = 0; i < roundElements.size(); i++) {
                    RoundDto round = new RoundDto(
                            title,
                            url,
                            date,
                            i + 1,
                            getInt(roundElements.get(i)),
                            getInt(ratingElements.get(i)),
                            getHoles(uo),
                            true,
                            false);
                    rounds.add(round);
                }
            }
        }

        return rounds;
    }

    private static String getText(Element e) {
        return e.text().trim();
    }

    private static String getLink(Element e) {
        return e.select("a").first().attr("abs:href");
    }

    private static int getInt(Element e) {
        try {
            return Integer.parseInt(e.text().trim());
        } catch (Exception ex) {
            return 0;
        }
    }

    private static boolean getBoolean(Element e) {
        if ("Yes".equals(e.text().trim())) {
            return true;
        } else {
            return false;
        }
    }

    private static String getDate(Element e) {
        try {
            e.select("span").remove();
            String text = e.text().trim();
            if (text.length() > 11) {
                text = text.substring(text.lastIndexOf(' ') + 1);
            }
            return text;
        } catch (Exception ex) {
            return null;
        }
    }

    private static int getHoles(String link) {
        try {
            Document doc = Jsoup.connect(link).get();
            return getHoles(doc);
        } catch (Exception e) {
            LOG.warn(String.format("Error getting holes from link: %s", link), e);
        }
        return 18;
    }

    private static int getHoles(Document doc) {
        try {
            Element template = doc.getElementsByClass("tooltip-templates").first();
            String[] splitted = template.text().trim().split(";");
            String holesText = splitted[1].trim();
            String[] splitted2 = holesText.split(" ");
            return Integer.parseInt(splitted2[0]);
        } catch (Exception e) {
            LOG.warn("Error parsing document", e);
        }
        return 18;
    }

    private static int calculateNextRating(List<RoundDto> rounds, boolean calculateDoubles, boolean byRoundsOnly) {
        Collections.sort(rounds);
        int includedRoundsSize = Math.toIntExact(rounds.stream().filter(RoundDto::isIncluded).count());
        LOG.debug(String.format("Included rounds: %d", includedRoundsSize));
        if(calculateDoubles) {
            int doubleRounds = includedRoundsSize >= 8 ? (int) Math.ceil(includedRoundsSize * 0.25) : 0;
            LOG.debug(String.format("Doubled rounds: %d", doubleRounds));
            List<RoundDto> lastRounds = rounds.subList(rounds.size() - doubleRounds, rounds.size());
            LOG.debug(String.format("Last rounds size: %d", lastRounds.size()));
            lastRounds.forEach(r -> r.setDoubled(true));
        }
        int ratingSum = 0;
        int ratingCount = 0;

        for (RoundDto r : rounds) {
            if (r.isIncluded()) {
                int holes = byRoundsOnly ? 1 : r.getHoles();
                int multiplier = r.isDoubled() ? 2 : 1;
                ratingSum += r.getRating() * holes * multiplier;
                ratingCount += holes * multiplier;
            }
        }

        LOG.debug(String.format("ratingSum: %d, ratingCount: %d", ratingSum, ratingCount));

        return Math.round((float) ratingSum / (float) ratingCount);
    }

    private static String getEventNumber(String link) {
        String event = link.substring(link.lastIndexOf('/') + 1);
        return event.contains("#") ? event.substring(0, event.lastIndexOf('#')) : event;
    }
}
