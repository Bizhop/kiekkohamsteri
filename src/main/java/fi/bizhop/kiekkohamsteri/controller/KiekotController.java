package fi.bizhop.kiekkohamsteri.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.KiekotListausProjection;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.KiekkoService;

@RestController
public class KiekotController extends BaseController {
	@Autowired
	KiekkoService kiekkoService;
	@Autowired
	AuthService authService;
	
	@RequestMapping(value = "/kiekot", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<KiekotListausProjection> haeKiekot(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("KiekotController.haeKiekot()...");
		
		Members owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return kiekkoService.haeKiekot(owner);
		}
	}
	
	@RequestMapping(value = "/kiekot/email", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<KiekotListausProjection> haeKiekotEmail(@RequestParam(value = "email") String email, HttpServletResponse response) {
		LOG.debug("KiekotController.haeKiekotEmail()...");
		
		Members owner = authService.getUser(email);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return kiekkoService.haeKiekot(owner);
		}
	}
	
	@RequestMapping(value = "/kiekot", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody KiekotListausProjection uusiKiekko(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("KiekotController.uusiKiekko()...");
		
		Members owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return kiekkoService.uusiKiekko(owner);
		}
	}
}
