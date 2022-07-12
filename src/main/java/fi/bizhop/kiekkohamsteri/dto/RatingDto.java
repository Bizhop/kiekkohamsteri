package fi.bizhop.kiekkohamsteri.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class RatingDto {
	private List<RoundDto> rounds;
	private int nextRating;
}
