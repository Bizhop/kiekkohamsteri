package fi.bizhop.kiekkohamsteri.dto;

import java.time.LocalDate;
import java.util.UUID;

public class RoundDto {
	private String id;
	private String tournament;
	private String link;
	private LocalDate date;
	private int round;
	private int score;
	private int rating;
	private int holes;
	private boolean included;
	
	public String getTournament() {
		return tournament;
	}
	
	public String getLink() {
		return link;
	}

	public LocalDate getDate() {
		return date;
	}

	public int getRound() {
		return round;
	}

	public int getScore() {
		return score;
	}

	public int getRating() {
		return rating;
	}

	public int getHoles() {
		return holes;
	}

	public boolean isIncluded() {
		return included;
	}
	
	public String getId() {
		return id;
	}

	public RoundDto(String tournament, String link, LocalDate date, int round, int score, int rating, int holes, boolean included) {
		this.id = UUID.randomUUID().toString();
		this.tournament = tournament;
		this.link = link;
		this.date = date;
		this.round = round;
		this.score = score;
		this.rating = rating;
		this.holes = holes;
		this.included = included;
	}
}
