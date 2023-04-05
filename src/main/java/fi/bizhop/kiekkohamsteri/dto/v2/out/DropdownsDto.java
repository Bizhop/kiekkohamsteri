package fi.bizhop.kiekkohamsteri.dto.v2.out;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.util.List;

@Value
@Builder
@Jacksonized
public class DropdownsDto {
	@NotNull
	List<DropdownOutputDto> manufacturers;
	@NotNull
	List<DropdownOutputDto> molds;
	@NotNull
	List<DropdownOutputDto> plastics;
	@NotNull
	List<DropdownOutputDto> colors;
	@NotNull
	List<DropdownOutputDto> conditions;
	@NotNull
	List<DropdownOutputDto> markings;
}
