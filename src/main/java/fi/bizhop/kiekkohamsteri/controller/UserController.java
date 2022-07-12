package fi.bizhop.kiekkohamsteri.controller;

import fi.bizhop.kiekkohamsteri.dto.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.v1.LeaderProjection;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.DiscService;
import fi.bizhop.kiekkohamsteri.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
	public @ResponseBody List<Members> getUsers(@RequestAttribute("user") Members user, HttpServletResponse response) {
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
	public @ResponseBody Members getDetails(@PathVariable Long id, @RequestAttribute("user") Members authUser, HttpServletResponse response) {
		var user = userService.getUser(id);

		if(!authUser.equals(user) && authUser.getLevel() != 2) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		return user;
	}

	@RequestMapping(value = "/user/{id}", method = PATCH, produces = "application/json", consumes = "application/json")
	public @ResponseBody Members updateDetails(@PathVariable Long id, @RequestBody UserUpdateDto dto, @RequestAttribute("user") Members authUser, HttpServletResponse response) {
		var user = userService.getUser(id);

		boolean adminRequest = authUser.getLevel() == 2;
		if(!authUser.equals(user) && !adminRequest) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		return userService.updateDetails(user, dto, adminRequest);
	}

	@RequestMapping(value = "/user/{id}/level/{level}", method = PATCH, produces = "application/json")
	@Deprecated
	public @ResponseBody Members setUserLevel(@PathVariable Long id, @PathVariable Integer level, @RequestAttribute("user") Members authUser, HttpServletResponse response) {
		if(authUser.getLevel() != 2) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		return userService.setUserLevel(id, level);
	}

	@RequestMapping(value = "/user/leaders", method = GET, produces = "application/json")
	public @ResponseBody List<LeaderProjection> getLeaders(HttpServletResponse response) {
		response.setStatus(SC_OK);
		updateDiscCounts(userService.getUsers());
		return userService.getLeaders();
	}

	@RequestMapping(value = "/user/me", method = GET, produces = "application/json")
	public @ResponseBody Members getMe(@RequestAttribute("user") Members user, HttpServletResponse response) {
		response.setStatus(SC_OK);
		return user;
	}

	private void updateDiscCounts(List<Members> users) {
		discService.updateDiscCounts(users);
		userService.saveUsers(users);
	}
}
