package fi.bizhop.kiekkohamsteri.dto.v2.out;

import fi.bizhop.kiekkohamsteri.model.Group;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class GroupDto {
    @NotNull
    Long id;
    String name;

    public static GroupDto fromDb(Group input) {
        if(input == null) return null;
        return GroupDto.builder()
                .id(input.getId())
                .name(input.getName())
                .build();
    }
}
