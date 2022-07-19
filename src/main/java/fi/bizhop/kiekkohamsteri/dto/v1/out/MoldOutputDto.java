package fi.bizhop.kiekkohamsteri.dto.v1.out;

import fi.bizhop.kiekkohamsteri.model.Mold;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class MoldOutputDto {
    Long id;
    ManufacturerOutputDto valmistaja;
    String kiekko;
    Double nopeus;
    Double liito;
    Double vakaus;
    Double feidi;

    public static MoldOutputDto fromDb(Mold input) {
        if(input == null) return null;
        return MoldOutputDto.builder()
                .id(input.getId())
                .valmistaja(ManufacturerOutputDto.fromDb(input.getManufacturer()))
                .kiekko(input.getName())
                .nopeus(input.getSpeed())
                .liito(input.getGlide())
                .vakaus(input.getStability())
                .feidi(input.getFade())
                .build();
    }
}
