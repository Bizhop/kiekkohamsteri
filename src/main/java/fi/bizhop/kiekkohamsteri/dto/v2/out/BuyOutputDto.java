package fi.bizhop.kiekkohamsteri.dto.v2.out;

import fi.bizhop.kiekkohamsteri.model.Buy;
import fi.bizhop.kiekkohamsteri.model.Buy.Status;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class BuyOutputDto {
    Long id;
    DiscOutputDto disc;
    UserOutputDto seller;
    UserOutputDto buyer;
    Status status;

    public static BuyOutputDto fromDb(Buy input) {
        if(input == null) return null;
        return BuyOutputDto.builder()
                .id(input.getId())
                .disc(DiscOutputDto.fromDb(input.getDisc()))
                .seller(UserOutputDto.fromDbCompact(input.getSeller()))
                .buyer(UserOutputDto.fromDbCompact(input.getBuyer()))
                .status(input.getStatus())
                .build();
    }
}
