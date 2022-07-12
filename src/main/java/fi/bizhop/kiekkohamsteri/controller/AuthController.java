package fi.bizhop.kiekkohamsteri.controller;

import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController {
	final AuthService authService;
	
	@RequestMapping(value = "/auth/login", method = RequestMethod.GET)
	public @ResponseBody Members login(HttpServletRequest request, HttpServletResponse response) {
		Members user = authService.login(request);
		if(user == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}
		else {
			response.setStatus(SC_OK);
			return user;
		}
	}
}
