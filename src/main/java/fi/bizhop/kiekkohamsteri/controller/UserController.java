package fi.bizhop.kiekkohamsteri.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.dto.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.MemberService;

@RestController
public class UserController extends BaseController {
	@Autowired
	AuthService authService;
	@Autowired
	MemberService memberService;
	
	@RequestMapping(value = "/user", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Members> getUsers(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("UserController.getUsers()...");
		
		Members owner = authService.getUser(request);
		if(owner == null || owner.getLevel() != 2) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return memberService.getUsers();
		}
	}
	
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Members getDetails(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug(String.format("UserController.getDetails(%d)...", id));
		
		Members authUser = authService.getUser(request);
		Members user = memberService.getUser(id);
		
		if(authUser != null && (authUser.equals(user) || authUser.getLevel() == 2)) {
			response.setStatus(HttpServletResponse.SC_OK);
			return memberService.getUser(id);
		}
		else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
	}
	
	@RequestMapping(value = "/user/{id}", method = RequestMethod.PATCH, produces = "application/json", consumes = "application/json")
	public @ResponseBody Members updateDetails(@PathVariable Long id, @RequestBody UserUpdateDto dto, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug(String.format("UserController.updateDetails(%d)...", id));
		
		Members authUser = authService.getUser(request);
		Members user = memberService.getUser(id);
		
		if(authUser != null && (authUser.equals(user) || authUser.getLevel() == 2)) {
			response.setStatus(HttpServletResponse.SC_OK);
			return memberService.updateDetails(id, dto);
		}
		else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
	}
	
	@RequestMapping(value = "/user/{id}/level/{level}", method = RequestMethod.PATCH, produces= "application/json") 
	public @ResponseBody Members setUserLevel(@PathVariable Long id, @PathVariable Integer level, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug(String.format("UserController.setUserLevel(%d, %d)...", id, level));
		
		Members authUser = authService.getUser(request);
		
		if(authUser == null || authUser.getLevel() != 2) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return memberService.setUserLevel(id, level);
		}
	}
}
