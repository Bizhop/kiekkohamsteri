package fi.bizhop.kiekkohamsteri.dto.v2.out;

import fi.bizhop.kiekkohamsteri.model.Role;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class RoleDto {
    @NotNull
    Long id;
    String name;
    Long groupId;

    public static RoleDto fromDb(Role input) {
        if(input == null) return null;
        return RoleDto.builder()
                .id(input.getId())
                .name(input.getName())
                .groupId(input.getGroupId())
                .build();
    }
}
