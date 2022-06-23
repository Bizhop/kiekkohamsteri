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

@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController {
	final AuthService authService;
	
	@RequestMapping(value = "/auth/login", method = RequestMethod.GET)
	public @ResponseBody Members login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Members user = authService.login(request);
		if(user == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			return user;
		}
	}
}
