package fi.bizhop.kiekkohamsteri.dto.v2.out;

import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class ManufacturerOutputDto {
    @NotNull
    Long id;
    @NotNull
    String name;

    public static ManufacturerOutputDto fromDb(Manufacturer input) {
        if(input == null) return null;
        return ManufacturerOutputDto.builder()
                .id(input.getId())
                .name(input.getName())
                .build();
    }
}
