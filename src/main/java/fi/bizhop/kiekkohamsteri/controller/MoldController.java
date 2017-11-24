package fi.bizhop.kiekkohamsteri.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.MoldProjection;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.MoldService;

@RestController
public class MoldController extends BaseController {
	@Autowired
	AuthService authService;
	@Autowired
	MoldService moldService;
	
	@RequestMapping(value="/molds", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody List<MoldProjection> getMolds(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("MoldController.getMolds()...");
		
		Members owner = authService.getUser(request);
		if(owner == null || owner.getLevel() != 2) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return moldService.getMolds();
		}
	}
}
