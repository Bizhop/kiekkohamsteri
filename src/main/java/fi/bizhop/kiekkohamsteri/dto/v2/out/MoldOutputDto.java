package fi.bizhop.kiekkohamsteri.dto.v2.out;

import fi.bizhop.kiekkohamsteri.model.Mold;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class MoldOutputDto {
    @NotNull
    Long id;
    @NotNull
    ManufacturerOutputDto manufacturer;
    @NotNull
    String name;
    @NotNull
    Double speed;
    @NotNull
    Double glide;
    @NotNull
    Double stability;
    @NotNull
    Double fade;

    public static MoldOutputDto fromDb(Mold input) {
        if(input == null) return null;
        return MoldOutputDto.builder()
                .id(input.getId())
                .manufacturer(ManufacturerOutputDto.fromDb(input.getManufacturer()))
                .name(input.getName())
                .speed(input.getSpeed())
                .glide(input.getGlide())
                .stability(input.getStability())
                .fade(input.getFade())
                .build();
    }
}
