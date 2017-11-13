package fi.bizhop.kiekkohamsteri.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.dto.KiekkoDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.KiekkoProjection;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.KiekkoService;

@RestController
public class KiekotController extends BaseController {
	@Autowired
	KiekkoService kiekkoService;
	@Autowired
	AuthService authService;
	
	@RequestMapping(value = "/kiekot", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Page<KiekkoProjection> haeKiekot(HttpServletRequest request, HttpServletResponse response, Pageable pageable) {
		LOG.debug("KiekotController.haeKiekot()...");
		
		Members owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return kiekkoService.haeKiekot(owner, pageable);
		}
	}
	
	@RequestMapping(value = "/kiekot", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody KiekkoProjection uusiKiekko(HttpServletRequest request, HttpServletResponse response) {
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
	
	@RequestMapping(value = "/kiekot/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public @ResponseBody KiekkoProjection paivitaKiekko(@PathVariable Long id, @RequestBody KiekkoDto dto, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("KiekotController.paivitaKiekko()...");
		
		Members owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			return kiekkoService.paivitaKiekko(dto, id, owner);
		}
		catch(AuthorizationException ae) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
	}
}
