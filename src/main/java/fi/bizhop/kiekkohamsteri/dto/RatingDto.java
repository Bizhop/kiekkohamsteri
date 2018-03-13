package fi.bizhop.kiekkohamsteri.dto;

import java.util.List;

public class RatingDto {
	private List<RoundDto> rounds;
	private int nextRating;
	
	public RatingDto(List<RoundDto> rounds, int nextRating) {
		this.rounds = rounds;
		this.nextRating = nextRating;
	}

	public List<RoundDto> getRounds() {
		return rounds;
	}

	public int getNextRating() {
		return nextRating;
	}
}
