package fi.bizhop.kiekkohamsteri.dto.v2.out;

import fi.bizhop.kiekkohamsteri.model.Color;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class ColorOutputDto {
    @NotNull
    Long id;
    @NotNull
    String name;

    public static ColorOutputDto fromDb(Color input) {
        if(input == null) return null;
        return ColorOutputDto.builder()
                .id(input.getId())
                .name(input.getName())
                .build();
    }
}
