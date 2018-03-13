package fi.bizhop.kiekkohamsteri.service;

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
			Element template = doc.getElementsByClass("tooltip-templates").first();
			String[] splitted = template.text().trim().split(";");
			String holesText = splitted[1].trim();
			String[] splitted2 = holesText.split(" ");
			return Integer.parseInt(splitted2[0]);
		} catch (Exception e) {
			return 18;
		}
	}
}
