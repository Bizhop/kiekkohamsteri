package fi.bizhop.kiekkohamsteri.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UploadDto {
	String data;
}
