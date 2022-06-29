package fi.bizhop.kiekkohamsteri.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UploadDto {
	String data;
	String name;
}
