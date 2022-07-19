package fi.bizhop.kiekkohamsteri.dto.v1;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class RatingDto {
	private List<RoundDto> rounds;
	private int nextRating;
}
