package fi.bizhop.kiekkohamsteri.dto.v2.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import fi.bizhop.kiekkohamsteri.model.Disc;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscOutputDto {
    @NotNull
    String uuid;
    @NotNull
    UserOutputDto owner;
    MoldOutputDto mold;
    PlasticOutputDto plastic;
    @NotNull
    ColorOutputDto color;
    @NotNull
    String image;
    Integer weight;
    @NotNull
    Integer condition;
    Boolean glow;
    Boolean special;
    @NotNull
    Boolean dyed;
    @NotNull
    Boolean swirly;
    @NotNull
    Integer markings;
    Boolean forSale;
    @NotNull
    Integer price;
    String description;
    Boolean lostAndFound;
    @NotNull
    Boolean itb;
    @NotNull
    Boolean publicDisc;
    @NotNull
    Boolean lost;
    @NotNull
    Date updatedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "fi_FI")
    public Date getUpdatedAt() {
        return updatedAt;
    }

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
                .updatedAt(input.getUpdatedAt())
                .build();
    }
}
