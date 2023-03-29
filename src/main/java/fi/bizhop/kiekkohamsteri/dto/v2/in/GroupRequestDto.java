package fi.bizhop.kiekkohamsteri.dto.v2.in;

import fi.bizhop.kiekkohamsteri.model.GroupRequest.Type;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class GroupRequestDto {
    @NotNull
    Long targetUserId;
    @NotNull
    Type type;
    String info;
}
