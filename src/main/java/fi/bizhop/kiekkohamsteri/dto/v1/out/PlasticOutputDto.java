package fi.bizhop.kiekkohamsteri.dto.v1.out;

import fi.bizhop.kiekkohamsteri.model.Plastic;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class PlasticOutputDto {
    Long id;
    ManufacturerOutputDto valmistaja;
    String muovi;

    public static PlasticOutputDto fromDb(Plastic input) {
        if(input == null) return null;
        return PlasticOutputDto.builder()
                .id(input.getId())
                .valmistaja(ManufacturerOutputDto.fromDb(input.getManufacturer()))
                .muovi(input.getName())
                .build();
    }
}
