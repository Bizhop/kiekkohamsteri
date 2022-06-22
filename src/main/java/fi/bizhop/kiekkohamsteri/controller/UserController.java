package fi.bizhop.kiekkohamsteri.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.dto.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.v1.LeaderProjection;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.UserService;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

@RestController
public class UserController extends BaseController {
	@Autowired
	AuthService authService;
	@Autowired
	UserService userService;
	
	@RequestMapping(value = "/user", method = GET, produces = "application/json")
	public @ResponseBody List<Members> getUsers(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("UserController.getUsers()...");
		
		Members owner = authService.getUser(request);
		if(owner == null || owner.getLevel() != 2) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(SC_OK);
			return userService.getUsers();
		}
	}
	
	@RequestMapping(value = "/user/{id}", method = GET, produces = "application/json")
	public @ResponseBody Members getDetails(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug(String.format("UserController.getDetails(%d)...", id));
		
		Members authUser = authService.getUser(request);
		Members user = userService.getUser(id);
		
		if(authUser != null && (authUser.equals(user) || authUser.getLevel() == 2)) {
			response.setStatus(SC_OK);
			return userService.getUser(id);
		}
		else {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}
	}
	
	@RequestMapping(value = "/user/{id}", method = PATCH, produces = "application/json", consumes = "application/json")
	public @ResponseBody Members updateDetails(@PathVariable Long id, @RequestBody UserUpdateDto dto, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug(String.format("UserController.updateDetails(%d)...", id));
		
		Members authUser = authService.getUser(request);
		Members user = userService.getUser(id);
		
		if(authUser != null && (authUser.equals(user) || authUser.getLevel() == 2)) {
			response.setStatus(SC_OK);
			return userService.updateDetails(id, dto);
		}
		else {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}
	}
	
	@RequestMapping(value = "/user/{id}/level/{level}", method = PATCH, produces = "application/json")
	public @ResponseBody Members setUserLevel(@PathVariable Long id, @PathVariable Integer level, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug(String.format("UserController.setUserLevel(%d, %d)...", id, level));
		
		Members authUser = authService.getUser(request);
		
		if(authUser == null || authUser.getLevel() != 2) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(SC_OK);
			return userService.setUserLevel(id, level);
		}
	}
	
	@RequestMapping(value = "/user/leaders", method = GET, produces = "application/json")
	public @ResponseBody List<LeaderProjection> getLeaders(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("UserController.getLeaders()...");
		
		Members authUser = authService.getUser(request);
		if(authUser == null) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(SC_OK);
			return userService.getLeaders();
		}
	}

	@RequestMapping(value = "/user/me", method = GET, produces = "application/json")
    public @ResponseBody Members getMe(HttpServletRequest request, HttpServletResponse response) {
	    LOG.debug("UserController.getMe()...");

	    Members authUser = authService.getUser(request);
	    response.setStatus(authUser == null ? SC_FORBIDDEN : SC_OK);
        return authUser;
    }
}
