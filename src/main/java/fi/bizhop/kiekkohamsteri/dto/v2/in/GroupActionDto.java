package fi.bizhop.kiekkohamsteri.dto.v2.in;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class GroupActionDto {
    Long userId;

}
