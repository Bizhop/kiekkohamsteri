package fi.bizhop.kiekkohamsteri.dto.v2.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.bizhop.kiekkohamsteri.model.User;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserOutputDto {
    @NotNull
    Long id;
    @NotNull
    String username;
    @NotNull
    String email;
    @NotNull
    String firstName;
    @NotNull
    String lastName;
    @NotNull
    Integer pdgaNumber;
    String jwt;
    Set<RoleDto> roles;
    Set<GroupDto> groups;

    String error;

    public static UserOutputDto fromDb(User input) {
        if(input == null) return null;
        return UserOutputDto.builder()
                .id(input.getId())
                .username(input.getUsername())
                .email(input.getEmail())
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .pdgaNumber(input.getPdgaNumber())
                .jwt(input.getJwt())
                .roles(input.getRoles() == null
                        ? new HashSet<>()
                        : input.getRoles().stream().map(RoleDto::fromDb).collect(Collectors.toSet()))
                .groups(input.getGroups() == null
                        ? new HashSet<>()
                        : input.getGroups().stream().map(GroupDto::fromDb).collect(Collectors.toSet()))
                .build();
    }

    public static UserOutputDto fromDbCompact(User input) {
        if(input == null) return null;
        return UserOutputDto.builder()
                .id(input.getId())
                .username(input.getUsername())
                .email(input.getEmail())
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .pdgaNumber(input.getPdgaNumber())
                .build();
    }
}
