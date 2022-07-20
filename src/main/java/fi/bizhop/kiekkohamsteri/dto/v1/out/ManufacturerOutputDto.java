package fi.bizhop.kiekkohamsteri.dto.v1.out;

import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ManufacturerOutputDto {
    Long id;
    String valmistaja;

    public static ManufacturerOutputDto fromDb(Manufacturer input) {
        if(input == null) return null;
        return ManufacturerOutputDto.builder()
                .id(input.getId())
                .valmistaja(input.getName())
                .build();
    }
}
