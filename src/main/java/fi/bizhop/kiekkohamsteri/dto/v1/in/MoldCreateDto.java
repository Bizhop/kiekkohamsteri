package fi.bizhop.kiekkohamsteri.dto.v1.in;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class MoldCreateDto {
	Long valmId;
	String kiekko;
	Double nopeus;
	Double liito;
	Double vakaus;
	Double feidi;
}
