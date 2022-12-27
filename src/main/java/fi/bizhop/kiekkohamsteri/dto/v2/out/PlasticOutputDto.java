package fi.bizhop.kiekkohamsteri.dto.v2.out;

import fi.bizhop.kiekkohamsteri.model.Plastic;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class PlasticOutputDto {
    Long id;
    ManufacturerOutputDto manufacturer;
    String name;

    public static PlasticOutputDto fromDb(Plastic input) {
        if(input == null) return null;
        return PlasticOutputDto.builder()
                .id(input.getId())
                .manufacturer(ManufacturerOutputDto.fromDb(input.getManufacturer()))
                .name(input.getName())
                .build();
    }
}
