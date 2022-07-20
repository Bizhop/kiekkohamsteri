package fi.bizhop.kiekkohamsteri.dto.v1.out;

import fi.bizhop.kiekkohamsteri.model.User;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserOutputDto {
    Long id;
    String username;
    String email;
    Integer level;
    String etunimi;
    String sukunimi;
    Integer pdga_num;
    String jwt;
    Boolean publicList;
    Boolean publicDiscCount;
    Integer discCount;

    public static UserOutputDto fromDb(User input) {
        if(input == null) return null;
        return UserOutputDto.builder()
                .id(input.getId())
                .username(input.getUsername())
                .email(input.getEmail())
                .level(input.getLevel())
                .etunimi(input.getFirstName())
                .sukunimi(input.getLastName())
                .pdga_num(input.getPdgaNumber())
                .jwt(input.getJwt())
                .publicList(input.getPublicList())
                .publicDiscCount(input.getPublicDiscCount())
                .discCount(input.getDiscCount())
                .build();
    }
}
