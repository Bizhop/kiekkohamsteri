package fi.bizhop.kiekkohamsteri.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MoldCreateDto {
	Long valmId;
	String kiekko;
	Double nopeus;
	Double liito;
	Double vakaus;
	Double feidi;
}
