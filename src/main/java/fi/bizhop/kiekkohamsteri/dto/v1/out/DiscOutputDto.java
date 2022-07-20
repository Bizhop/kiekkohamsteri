package fi.bizhop.kiekkohamsteri.dto.v1.out;

import fi.bizhop.kiekkohamsteri.model.Disc;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class DiscOutputDto {
    Long id;
    UserOutputDto member;
    MoldOutputDto mold;
    PlasticOutputDto muovi;
    ColorOutputDto vari;
    String kuva;
    Integer paino;
    Integer kunto;
    Boolean hohto;
    Boolean spessu;
    Boolean dyed;
    Boolean swirly;
    Integer tussit;
    Boolean myynnissa;
    Integer hinta;
    String muuta;
    Boolean loytokiekko;
    Boolean itb;
    Boolean publicDisc;
    Boolean lost;

    public static DiscOutputDto fromDb(Disc input) {
        if(input == null) return null;
        return DiscOutputDto.builder()
                .id(input.getId())
                .member(UserOutputDto.fromDb(input.getOwner()))
                .mold(MoldOutputDto.fromDb(input.getMold()))
                .muovi(PlasticOutputDto.fromDb(input.getPlastic()))
                .vari(ColorOutputDto.fromDb(input.getColor()))
                .kuva(input.getImage())
                .paino(input.getWeight())
                .kunto(input.getCondition())
                .hohto(input.getGlow())
                .spessu(input.getSpecial())
                .dyed(input.getDyed())
                .swirly(input.getSwirly())
                .tussit(input.getMarkings())
                .myynnissa(input.getForSale())
                .hinta(input.getPrice())
                .muuta(input.getDescription())
                .loytokiekko(input.getLostAndFound())
                .itb(input.getItb())
                .publicDisc(input.getPublicDisc())
                .lost(input.getLost())
                .build();
    }
}
