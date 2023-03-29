package fi.bizhop.kiekkohamsteri.dto.v2.out;

import fi.bizhop.kiekkohamsteri.model.GroupRequest;
import fi.bizhop.kiekkohamsteri.model.GroupRequest.Status;
import fi.bizhop.kiekkohamsteri.model.GroupRequest.Type;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class GroupRequestOutputDto {
    @NotNull
    Long id;
    @NotNull
    GroupDto group;
    @NotNull
    UserOutputDto source;
    @NotNull
    UserOutputDto target;
    @NotNull
    Type type;
    @NotNull
    Status status;
    String info;

    String error;

    public static GroupRequestOutputDto fromDb(GroupRequest input) {
        if(input == null) return null;
        return builderFromDb(input)
                .build();
    }

    public static GroupRequestOutputDtoBuilder builderFromDb(GroupRequest input) {
        if(input == null) return GroupRequestOutputDto.builder();
        return GroupRequestOutputDto.builder()
                .id(input.getId())
                .group(GroupDto.fromDb(input.getGroup()))
                .source(UserOutputDto.fromDb(input.getSource()))
                .target(UserOutputDto.fromDb(input.getTarget()))
                .type(input.getType())
                .status(input.getStatus())
                .info(input.getInfo());
    }
}
