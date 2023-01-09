package fi.bizhop.kiekkohamsteri.dto.v2.out;

import fi.bizhop.kiekkohamsteri.model.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class DropdownOutputDto {
    Long value;
    String name;

    public static DropdownOutputDto fromDropdownInterface(Dropdown input) {
        if(input == null) return null;
        return DropdownOutputDto.builder()
                .value(input.getValue())
                .name(input.getName())
                .build();
    }
}
