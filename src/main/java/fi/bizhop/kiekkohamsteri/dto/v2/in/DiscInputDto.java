package fi.bizhop.kiekkohamsteri.dto.v2.in;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class DiscInputDto {
    Long moldId;
    Long plasticId;
    Long colorId;
    String image;
    Integer weight;
    Integer condition;
    Boolean glow;
    Boolean special;
    Boolean dyed;
    Boolean swirly;
    Integer markings;
    Boolean forSale;
    Integer price;
    String description;
    Boolean lostAndFound;
    Boolean itb;
    Boolean publicDisc;
    Boolean lost;

    public static DiscInputDto fromV1(fi.bizhop.kiekkohamsteri.dto.v1.in.DiscInputDto v1) {
        return DiscInputDto.builder()
                .moldId(v1.getMoldId())
                .plasticId(v1.getMuoviId())
                .colorId(v1.getVariId())
                .image(v1.getKuva())
                .weight(v1.getPaino())
                .condition(v1.getKunto())
                .glow(v1.getHohto())
                .special(v1.getSpessu())
                .dyed(v1.getDyed())
                .swirly(v1.getSwirly())
                .markings(v1.getTussit())
                .forSale(v1.getMyynnissa())
                .price(v1.getHinta())
                .description(v1.getMuuta())
                .lostAndFound(v1.getLoytokiekko())
                .itb(v1.getItb())
                .publicDisc(v1.getPublicDisc())
                .lost(v1.getLost())
                .build();
    }
}
