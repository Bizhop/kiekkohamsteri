package fi.bizhop.kiekkohamsteri.dto.v2.in;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class MoldCreateDto {
	Long manufacturerId;
	String name;
	Double speed;
	Double glide;
	Double stability;
	Double fade;
}
