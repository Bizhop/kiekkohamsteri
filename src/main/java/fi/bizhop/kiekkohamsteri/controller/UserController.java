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
	public @ResponseBody List<Members> getUsers(HttpServletRequest request, HttpServletResponse response) {
		var user = authService.getUser(request);
		if(user == null || user.getLevel() != 2) {
			response.setStatus(user == null ? SC_UNAUTHORIZED : SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		var users = userService.getUsers();
		updateDiscCounts(users);
		return users;
	}

	@RequestMapping(value = "/user/{id}", method = GET, produces = "application/json")
	public @ResponseBody Members getDetails(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		var authUser = authService.getUser(request);
		var user = userService.getUser(id);

		if(authUser == null || (!authUser.equals(user) && (authUser.getLevel() != 2))) {
			response.setStatus(authUser == null ? SC_UNAUTHORIZED : SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		return user;
	}

	@RequestMapping(value = "/user/{id}", method = PATCH, produces = "application/json", consumes = "application/json")
	public @ResponseBody Members updateDetails(@PathVariable Long id, @RequestBody UserUpdateDto dto, HttpServletRequest request, HttpServletResponse response) {
		var authUser = authService.getUser(request);
		var user = userService.getUser(id);

		if(authUser == null || ((!authUser.equals(user) && (authUser.getLevel() != 2)))) {
			response.setStatus(authUser == null ? SC_UNAUTHORIZED : SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		return userService.updateDetails(user, dto);
	}

	@RequestMapping(value = "/user/{id}/level/{level}", method = PATCH, produces = "application/json")
	public @ResponseBody Members setUserLevel(@PathVariable Long id, @PathVariable Integer level, HttpServletRequest request, HttpServletResponse response) {
		var authUser = authService.getUser(request);

		if(authUser == null || authUser.getLevel() != 2) {
			response.setStatus(authUser == null ? SC_UNAUTHORIZED : SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		return userService.setUserLevel(id, level);
	}

	@RequestMapping(value = "/user/leaders", method = GET, produces = "application/json")
	public @ResponseBody List<LeaderProjection> getLeaders(HttpServletRequest request, HttpServletResponse response) {
		var authUser = authService.getUser(request);
		if(authUser == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}

		response.setStatus(SC_OK);
		updateDiscCounts(userService.getUsers());
		return userService.getLeaders();
	}

	@RequestMapping(value = "/user/me", method = GET, produces = "application/json")
	public @ResponseBody Members getMe(HttpServletRequest request, HttpServletResponse response) {
		var authUser = authService.getUser(request);
		response.setStatus(authUser == null ? SC_UNAUTHORIZED : SC_OK);
		return authUser;
	}

	private void updateDiscCounts(List<Members> users) {
		discService.updateDiscCounts(users);
		userService.saveUsers(users);
	}
}
