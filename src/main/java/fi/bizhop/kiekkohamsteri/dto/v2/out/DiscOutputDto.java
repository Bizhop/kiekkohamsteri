package fi.bizhop.kiekkohamsteri.dto.v2.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.bizhop.kiekkohamsteri.model.Disc;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscOutputDto {
    String uuid;
    UserOutputDto owner;
    MoldOutputDto mold;
    PlasticOutputDto plastic;
    ColorOutputDto color;
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

    public static DiscOutputDto fromDb(Disc input) {
        if(input == null) return null;
        return DiscOutputDto.builder()
                .uuid(input.getUuid())
                .owner(UserOutputDto.fromDbCompact(input.getOwner()))
                .mold(MoldOutputDto.fromDb(input.getMold()))
                .plastic(PlasticOutputDto.fromDb(input.getPlastic()))
                .color(ColorOutputDto.fromDb(input.getColor()))
                .image(input.getImage())
                .weight(input.getWeight())
                .condition(input.getCondition())
                .glow(input.getGlow())
                .special(input.getSpecial())
                .dyed(input.getDyed())
                .swirly(input.getSwirly())
                .markings(input.getMarkings())
                .forSale(input.getForSale())
                .price(input.getPrice())
                .description(input.getDescription())
                .lostAndFound(input.getLostAndFound())
                .itb(input.getItb())
                .publicDisc(input.getPublicDisc())
                .lost(input.getLost())
                .build();
    }
}
