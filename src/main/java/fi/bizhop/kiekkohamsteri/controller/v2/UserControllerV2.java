package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.LeaderDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.UserOutputDto;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.DiscService;
import fi.bizhop.kiekkohamsteri.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static fi.bizhop.kiekkohamsteri.util.Utils.userIsAdmin;
import static fi.bizhop.kiekkohamsteri.util.Utils.userIsGroupAdmin;
import static javax.servlet.http.HttpServletResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

@RestController
@RequiredArgsConstructor
public class UserControllerV2 extends BaseControllerV2 {
    final AuthService authService;
    final UserService userService;
    final DiscService discService;

    @RequestMapping(value = "/user", method = GET, produces = "application/json")
    public @ResponseBody List<UserOutputDto> getUsers(
            @RequestAttribute("user") User user,
            @RequestParam(required = false) Long groupId,
            HttpServletResponse response) {
        if(groupId == null) {
            if(!userIsAdmin(user)) {
                response.setStatus(SC_FORBIDDEN);
                return null;
            }
            response.setStatus(SC_OK);
            return usersWithUpdatedDiscCounts(userService.getUsers())
                    .stream()
                    .map(UserOutputDto::fromDb)
                    .collect(Collectors.toList());
        }

        if(!userIsGroupAdmin(user, groupId) && !userIsAdmin(user)) {
            response.setStatus(SC_FORBIDDEN);
            return null;
        }

        response.setStatus(SC_OK);
        return usersWithUpdatedDiscCounts(userService.getUsersByGroupId(groupId))
                .stream()
                .map(UserOutputDto::fromDb)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/user/{id}", method = GET, produces = "application/json")
    public @ResponseBody UserOutputDto getDetails(@PathVariable Long id, @RequestAttribute("user") User authUser, HttpServletResponse response) {
        var user = userService.getUser(id);

        if(!authUser.equals(user) && !userIsAdmin(authUser)) {
            response.setStatus(SC_FORBIDDEN);
            return null;
        }

        response.setStatus(SC_OK);
        return UserOutputDto.fromDb(user);
    }

    @RequestMapping(value = "/user/{id}", method = PATCH, consumes = "application/json", produces = "application/json")
    public @ResponseBody UserOutputDto updateDetails(@PathVariable Long id, @RequestBody UserUpdateDto dto, @RequestAttribute("user") User authUser, HttpServletResponse response) {
        var user = userService.getUser(id);

        boolean adminRequest = userIsAdmin(authUser);
        boolean groupAdminRequest = userIsGroupAdmin(authUser, dto.getRoleGroupId())
                || userIsGroupAdmin(authUser, dto.getAddToGroupId())
                || userIsGroupAdmin(authUser, dto.getRemoveFromGroupId());
        if(!authUser.equals(user) && !adminRequest && !groupAdminRequest) {
            response.setStatus(SC_FORBIDDEN);
            return null;
        }

        response.setStatus(SC_OK);
        return UserOutputDto.fromDb(userService.updateDetailsV2(user, authUser, dto, adminRequest));
    }

    @RequestMapping(value = "/user/leaders", method = GET, produces = "application/json")
    public @ResponseBody List<LeaderDto> getLeaders(HttpServletResponse response) {
        response.setStatus(SC_OK);
        return usersWithUpdatedDiscCounts(userService.getUsers()).stream()
                .map(LeaderDto::fromDb)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/user/me", method = GET, produces = "application/json")
    public @ResponseBody UserOutputDto getMe(@RequestAttribute("user") User user, HttpServletResponse response) {
        response.setStatus(SC_OK);
        return UserOutputDto.fromDb(user);
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.GET)
    public @ResponseBody UserOutputDto login(HttpServletRequest request, HttpServletResponse response) {
        User user = authService.login(request);
        if(user == null) {
            response.setStatus(SC_UNAUTHORIZED);
            return null;
        }

        response.setStatus(SC_OK);
        return UserOutputDto.fromDb(user);
    }

    // HELPER METHODS

    private List<User> usersWithUpdatedDiscCounts(List<User> users) {
        discService.updateDiscCounts(users);
        userService.saveUsers(users);
        return users;
    }
}
