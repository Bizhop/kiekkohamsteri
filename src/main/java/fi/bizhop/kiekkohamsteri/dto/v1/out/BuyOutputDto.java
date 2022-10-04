package fi.bizhop.kiekkohamsteri.dto.v1.out;

import fi.bizhop.kiekkohamsteri.model.Buy;
import fi.bizhop.kiekkohamsteri.model.Buy.Status;
import fi.bizhop.kiekkohamsteri.dto.v2.out.UserOutputDto;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class BuyOutputDto {
    Long id;
    DiscOutputDto kiekko;
    UserOutputDto myyja;
    UserOutputDto ostaja;
    Status status;

    public static BuyOutputDto fromDb(Buy input) {
        if(input == null) return null;
        return BuyOutputDto.builder()
                .id(input.getId())
                .kiekko(DiscOutputDto.fromDb(input.getDisc()))
                .myyja(UserOutputDto.fromDb(input.getSeller()))
                .ostaja(UserOutputDto.fromDb(input.getBuyer()))
                .status(input.getStatus())
                .build();
    }
}
