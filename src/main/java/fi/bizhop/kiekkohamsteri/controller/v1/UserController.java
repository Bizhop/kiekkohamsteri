package fi.bizhop.kiekkohamsteri.controller.v1;

import fi.bizhop.kiekkohamsteri.dto.v1.in.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.projection.v1.LeaderProjection;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.DiscService;
import fi.bizhop.kiekkohamsteri.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

@RestController
@RequiredArgsConstructor
public class UserController extends BaseController {
	final AuthService authService;
	final UserService userService;
	final DiscService discService;

	@RequestMapping(value = "/user", method = GET, produces = "application/json")
	public @ResponseBody List<User> getUsers(@RequestAttribute("user") User user, HttpServletResponse response) {
		if(user.getLevel() != 2) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		var users = userService.getUsers();
		updateDiscCounts(users);
		return users;
	}

	@RequestMapping(value = "/user/{id}", method = GET, produces = "application/json")
	public @ResponseBody
	User getDetails(@PathVariable Long id, @RequestAttribute("user") User authUser, HttpServletResponse response) {
		var user = userService.getUser(id);

		if(!authUser.equals(user) && authUser.getLevel() != 2) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		return user;
	}

	@RequestMapping(value = "/user/{id}", method = PATCH, produces = "application/json", consumes = "application/json")
	public @ResponseBody
	User updateDetails(@PathVariable Long id, @RequestBody UserUpdateDto dto, @RequestAttribute("user") User authUser, HttpServletResponse response) {
		var user = userService.getUser(id);

		boolean adminRequest = authUser.getLevel() == 2;
		if(!authUser.equals(user) && !adminRequest) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		var result = userService.updateDetails(user, dto, adminRequest);
		if(dto.isPublicList()) {
			discService.makeDiscsPublic(user);
		}
		return result;
	}

	@RequestMapping(value = "/user/{id}/level/{level}", method = PATCH, produces = "application/json")
	@Deprecated
	public @ResponseBody User setUserLevel(@PathVariable Long id, @PathVariable Integer level, @RequestAttribute("user") User authUser, HttpServletResponse response) {
		if(authUser.getLevel() != 2) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		var user = userService.getUser(id);
		var dto = UserUpdateDto.builder().level(level).build();
		return userService.updateDetails(user, dto, true);
	}

	@RequestMapping(value = "/user/leaders", method = GET, produces = "application/json")
	public @ResponseBody List<LeaderProjection> getLeaders(HttpServletResponse response) {
		response.setStatus(SC_OK);
		updateDiscCounts(userService.getUsers());
		return userService.getLeaders();
	}

	@RequestMapping(value = "/user/me", method = GET, produces = "application/json")
	public @ResponseBody
	User getMe(@RequestAttribute("user") User user, HttpServletResponse response) {
		response.setStatus(SC_OK);
		return user;
	}

	private void updateDiscCounts(List<User> users) {
		discService.updateDiscCounts(users);
		userService.saveUsers(users);
	}
}
