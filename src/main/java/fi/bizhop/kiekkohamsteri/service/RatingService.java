package fi.bizhop.kiekkohamsteri.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.dto.RoundDto;

@Service
public class RatingService {
	
	private static final String RATINGS_URL = "https://www.pdga.com/player/%s/details";
	private static final String PLAYER_URL = "https://www.pdga.com/player/%s";
	
	public List<RoundDto> getRounds(String pdga_num) throws Exception {
		Document doc = Jsoup.connect(String.format(RATINGS_URL, pdga_num)).get();
		Elements rows = doc.getElementById("player-results-details").select("tbody").select("tr");

		List<RoundDto> rounds = new ArrayList<>();
		for(Element row : rows) {
			Elements tds = row.select("td");
			if(tds.size() == 6) {
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
		
		Document info = Jsoup.connect(String.format(PLAYER_URL, pdga_num)).get();
		Element recent = info.getElementsByClass("recent-events").first();
		Elements links = recent.select("a");
		for(Element l : links) {
			List<RoundDto> unofficial = getUnofficial(l.attr("abs:href"), pdga_num);
			if(unofficial != null) {
				rounds.addAll(unofficial);
			}
		}
		
		return rounds;
	}
	
	private static List<RoundDto> getUnofficial(String url, String pdga) {
		try {
			List<RoundDto> rounds = new ArrayList<>();
			Document uo = Jsoup.connect(url).get();
			String title = uo.getElementsByClass("pane-page-title").first().select("h1").first().text().trim();
			LocalDate date = getDate(uo.getElementsByClass("tournament-date").first());
			Elements rows = uo.select("tr");
			for(Element row : rows) {
				Element searchPdga = row.getElementsByClass("pdga-number").first();
				if(searchPdga != null && pdga.equals(searchPdga.text().trim())) {
					Elements roundElements = row.getElementsByClass("round");
					Elements ratingElements = row.getElementsByClass("round-rating");
					for(int i=0; i<roundElements.size(); i++) {
						RoundDto round = new RoundDto(
								title,
								url,
								date,
								i+1,
								getInt(roundElements.get(i)),
								getInt(ratingElements.get(i)),
								getHoles(uo),
								false
								);
						rounds.add(round);
					}
				}
			}
			
			return rounds;
		} catch (IOException e) {
			return null;
		}
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
		}
		catch (Exception ex) {
			return 0;
		}
	}

	private static boolean getBoolean(Element e) {
		if("Yes".equals(e.text().trim())) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private static LocalDate getDate(Element e) {
		try {
			e.select("span").remove();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy");
			String text = e.text().trim();
			if(text.length() > 11) {
				text = text.substring(text.lastIndexOf(' ') + 1, text.length());
			}
			return LocalDate.parse(text, formatter);
		}
		catch (Exception ex) {
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
}
