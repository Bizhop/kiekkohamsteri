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
}
