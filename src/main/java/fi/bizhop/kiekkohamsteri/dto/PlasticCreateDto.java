package fi.bizhop.kiekkohamsteri.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlasticCreateDto {
	Long valmId;
	String muovi;
}
