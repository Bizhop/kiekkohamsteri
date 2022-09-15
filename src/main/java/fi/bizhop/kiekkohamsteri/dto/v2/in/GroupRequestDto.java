package fi.bizhop.kiekkohamsteri.dto.v2.in;

import fi.bizhop.kiekkohamsteri.model.GroupRequest.Type;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class GroupRequestDto {
    Long targetUserId;
    Type type;
    String info;
}
