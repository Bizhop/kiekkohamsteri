package fi.bizhop.kiekkohamsteri.dto.v2.out;

import fi.bizhop.kiekkohamsteri.model.Color;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ColorOutputDto {
    Long id;
    String name;

    public static ColorOutputDto fromDb(Color input) {
        if(input == null) return null;
        return ColorOutputDto.builder()
                .id(input.getId())
                .name(input.getName())
                .build();
    }
}
