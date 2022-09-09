package fi.bizhop.kiekkohamsteri.dto.v2.out;

import fi.bizhop.kiekkohamsteri.model.User;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class LeaderDto {
    String userName;
    Integer discCount;

    public static LeaderDto fromDb(User input) {
        if(input == null) return null;
        return LeaderDto.builder()
                .userName(input.getUsername())
                .discCount(input.getDiscCount())
                .build();
    }
}
