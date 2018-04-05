package fi.bizhop.kiekkohamsteri.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.dto.OstotDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.model.Ostot;
import fi.bizhop.kiekkohamsteri.model.Ostot.Status;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.OstoService;

@RestController
public class OstoController extends BaseController {
	@Autowired
	OstoService ostoService;
	@Autowired
	AuthService authService;

	@RequestMapping(value = "/ostot", method = RequestMethod.GET)
	public @ResponseBody List<Ostot> list(@RequestParam(value = "status", required = false) Status status ) {
		if(status == null) {
			return ostoService.list();
		}
		else {
			return ostoService.list(status);
		}
	}
	
	@RequestMapping(value = "/ostot/omat", method = RequestMethod.GET)
	public @ResponseBody OstotDto omat(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("OstoController.omat()...");
		
		Members user = authService.getUser(request);
		if(user == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			return ostoService.yhteenveto(user);
		}
	}

	@RequestMapping(value = "/ostot/{id}/confirm", method = RequestMethod.POST)
	public void confirm(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug(String.format("OstoController.confirm(%d)...", id));

		Members user = authService.getUser(request);
		if(user == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		else {
			try {
				ostoService.confirm(id, user);
			}
			catch (AuthorizationException e) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
		}
	}
	
	@RequestMapping(value = "/ostot/{id}/reject", method = RequestMethod.POST)
	public void reject(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug(String.format("OstoController.reject(%d)...", id));

		Members user = authService.getUser(request);
		if(user == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		else {
			try {
				ostoService.reject(id, user);
			}
			catch (AuthorizationException e) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
		}
	}
}