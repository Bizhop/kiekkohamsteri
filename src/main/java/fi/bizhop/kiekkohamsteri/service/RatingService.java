package fi.bizhop.kiekkohamsteri.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.dto.RatingDto;
import fi.bizhop.kiekkohamsteri.dto.RoundDto;

@Service
public class RatingService {
    private static final Logger LOG = Logger.getLogger(RatingService.class);

    private static final String RATINGS_URL = "https://www.pdga.com/player/%s/details";
    private static final String PLAYER_URL = "https://www.pdga.com/player/%s";

    public RatingDto getRounds(String pdga_num) throws Exception {
        List<RoundDto> rounds = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(String.format(RATINGS_URL, pdga_num)).get();
            Elements rows = doc.getElementById("player-results-details").select("tbody").select("tr");
            for (Element row : rows) {
                Elements tds = row.select("td");
                if (tds.size() == 6) {
                    rounds.add(new RoundDto(
                        getText(tds.get(0)),
                        getLink(tds.get(0)),
                        getDate(tds.get(1)),
                        getInt(tds.get(2)),
                        getInt(tds.get(3)),
                        getInt(tds.get(4)),
                        getHoles(getLink(tds.get(0))),
                        getBoolean(tds.get(5))
                    ));
                }
            }
        } catch (Exception e) {
            LOG.error("Virallisten kierrosten haku epäonnistui", e);
            throw new Exception("Internal error");
        }

        try {
            Document info = Jsoup.connect(String.format(PLAYER_URL, pdga_num)).get();
            Element recent = info.getElementsByClass("recent-events").first();
            if (recent != null) {
                Elements links = recent.select("a");
                for (Element l : links) {
                    rounds.addAll(getUnofficial(l.attr("abs:href"), pdga_num));
                }
            }
        } catch (Exception e) {
            LOG.error("Epävirallisten kierrosten haku epäonnistui", e);
        }

        return getRating(rounds, true);
    }

    public RatingDto getRating(List<RoundDto> rounds, boolean calculateDoubles) {
        int nextRating = calculateNextRating(rounds, calculateDoubles);

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
                        true
                    );
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
                text = text.substring(text.lastIndexOf(' ') + 1, text.length());
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
            System.out.println(e);
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
            System.out.println(e);
        }
        return 18;
    }

    private static int calculateNextRating(List<RoundDto> rounds, boolean calculateDoubles) {
        if(calculateDoubles) {
            int doubleRounds = rounds.size() >= 8 ? (int) Math.ceil(rounds.size() * 0.25) : 0;
            for (int i = 0; i < rounds.size(); i++) {
                rounds.get(i).setDoubled(i + doubleRounds >= rounds.size());
            }
        }
        int totalRating = 0;
        int totalHoles = 0;

        for (RoundDto r : rounds) {
            if (r.isIncluded()) {
                int multiplier = r.isDoubled() ? 2 : 1;
                totalRating += r.getRating() * r.getHoles() * multiplier;
                totalHoles += r.getHoles() * multiplier;
            }
        }

        return Math.round((float) totalRating / (float) totalHoles);
    }
}
