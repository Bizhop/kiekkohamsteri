package fi.bizhop.kiekkohamsteri.dto.v2.out;

import fi.bizhop.kiekkohamsteri.model.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class DropdownOutputDto {
    @NotNull
    Long value;
    @NotNull
    String name;

    public static DropdownOutputDto fromDropdownInterface(Dropdown input) {
        if(input == null) return null;
        return DropdownOutputDto.builder()
                .value(input.getValue())
                .name(input.getName())
                .build();
    }
}
