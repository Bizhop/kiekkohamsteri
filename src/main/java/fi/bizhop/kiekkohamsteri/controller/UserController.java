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
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}
		if(owner.getLevel() != 2) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		var users = userService.getUsers();
		discService.updateDiscCounts(users);
		userService.saveUsers(users);
		return userService.getUsers();
	}

	@RequestMapping(value = "/user/{id}", method = GET, produces = "application/json")
	public @ResponseBody Members getDetails(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		var authUser = authService.getUser(request);
		var user = userService.getUser(id);

		if(authUser == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}
		else if(!authUser.equals(user) || (authUser.getLevel() != 2)) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		return userService.getUser(id);
	}

	@RequestMapping(value = "/user/{id}", method = PATCH, produces = "application/json", consumes = "application/json")
	public @ResponseBody Members updateDetails(@PathVariable Long id, @RequestBody UserUpdateDto dto, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug(String.format("UserController.updateDetails(%d)...", id));

		var authUser = authService.getUser(request);
		var user = userService.getUser(id);

		if(authUser == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}
		else if(!authUser.equals(user) || (authUser.getLevel() != 2)) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}

		response.setStatus(SC_OK);
		return userService.updateDetails(id, dto);
	}

	@RequestMapping(value = "/user/{id}/level/{level}", method = PATCH, produces = "application/json")
	public @ResponseBody Members setUserLevel(@PathVariable Long id, @PathVariable Integer level, HttpServletRequest request, HttpServletResponse response) {
		var authUser = authService.getUser(request);

		if(authUser == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}
		else if(authUser.getLevel() != 2) {
			response.setStatus(SC_FORBIDDEN);
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
		return userService.getLeaders();
	}

	@RequestMapping(value = "/user/me", method = GET, produces = "application/json")
	public @ResponseBody Members getMe(HttpServletRequest request, HttpServletResponse response) {
		var authUser = authService.getUser(request);
		response.setStatus(authUser == null ? SC_UNAUTHORIZED : SC_OK);
		return authUser;
	}
}
