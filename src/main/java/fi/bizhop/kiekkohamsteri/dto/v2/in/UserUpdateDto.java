package fi.bizhop.kiekkohamsteri.dto.v2.in;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserUpdateDto {
    String username;
    String firstName;
    String lastName;
    Integer PDGANumber;
    boolean publicDiscCount;
    boolean publicList;
    Integer level;
    Long addToGroupId;
    Long removeFromGroupId;
    String addToRole;
    String removeFromRole;
    Long roleGroupId;

    public static UserUpdateDto fromV1(fi.bizhop.kiekkohamsteri.dto.v1.in.UserUpdateDto v1) {
        if(v1 == null) return null;
        return UserUpdateDto.builder()
                .username(v1.getUsername())
                .firstName(v1.getEtunimi())
                .lastName(v1.getSukunimi())
                .PDGANumber(v1.getPdga_num())
                .publicDiscCount(v1.isPublicDiscCount())
                .publicList(v1.isPublicList())
                .level(v1.getLevel())
                .build();
    }
}
