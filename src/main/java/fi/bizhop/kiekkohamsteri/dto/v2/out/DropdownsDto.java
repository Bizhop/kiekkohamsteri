package fi.bizhop.kiekkohamsteri.dto.v2.out;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class DropdownsDto {
	List<DropdownOutputDto> manufacturers;
	List<DropdownOutputDto> molds;
	List<DropdownOutputDto> plastics;
	List<DropdownOutputDto> colors;
	List<DropdownOutputDto> conditions;
	List<DropdownOutputDto> markings;
}
