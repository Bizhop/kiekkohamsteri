package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.controller.BaseControllerV2;
import fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.UserOutputDto;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static fi.bizhop.kiekkohamsteri.util.Utils.userBelongsToGroup;
import static fi.bizhop.kiekkohamsteri.util.Utils.userIsAdmin;
import static javax.servlet.http.HttpServletResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

@RestController
@RequiredArgsConstructor
public class UserController extends BaseControllerV2 {
    final AuthService authService;
    final UserService userService;

    @RequestMapping(value = "/user", method = GET, produces = "application/json")
    public @ResponseBody Page<UserOutputDto> getUsers(
            @RequestAttribute("user") User user,
            @RequestParam(required = false) Long groupId,
            HttpServletResponse response,
            @ParameterObject Pageable pageable) {
        if(groupId == null) {
            if(!userIsAdmin(user)) {
                response.setStatus(SC_FORBIDDEN);
                return null;
            }
            response.setStatus(SC_OK);
            return userService.getUsersPaging(pageable)
                    .map(UserOutputDto::fromDb);
        }

        if(!userBelongsToGroup(user, groupId) && !userIsAdmin(user)) {
            response.setStatus(SC_FORBIDDEN);
            return null;
        }

        response.setStatus(SC_OK);
        return userService.getUsersByGroupIdPaging(groupId, pageable)
                .map(UserOutputDto::fromDb);
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
        if(!authUser.equals(user) && !adminRequest) {
            response.setStatus(SC_FORBIDDEN);
            return null;
        }

        response.setStatus(SC_OK);
        return UserOutputDto.fromDb(userService.updateDetails(user, dto, adminRequest));
    }

    @RequestMapping(value = "/user/me", method = GET, produces = "application/json")
    public @ResponseBody UserOutputDto getMe(@RequestAttribute("user") User user, HttpServletResponse response) {
        response.setStatus(SC_OK);
        return UserOutputDto.fromDb(user);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public @ResponseBody UserOutputDto login(HttpServletRequest request, HttpServletResponse response) {
        User user = authService.login(request);
        if(user == null) {
            response.setStatus(SC_UNAUTHORIZED);
            return null;
        }

        response.setStatus(SC_OK);
        return UserOutputDto.fromDb(user);
    }
}
