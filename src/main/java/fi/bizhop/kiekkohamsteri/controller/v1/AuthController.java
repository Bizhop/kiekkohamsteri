package fi.bizhop.kiekkohamsteri.controller.v1;

import fi.bizhop.kiekkohamsteri.dto.v1.out.UserOutputDto;
import fi.bizhop.kiekkohamsteri.model.User;
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
	public @ResponseBody UserOutputDto login(HttpServletRequest request, HttpServletResponse response) {
		User user = authService.login(request);
		if(user == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}
		else {
			response.setStatus(SC_OK);
			return UserOutputDto.fromDb(user);
		}
	}
}
