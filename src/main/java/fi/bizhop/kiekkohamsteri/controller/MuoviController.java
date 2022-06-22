package fi.bizhop.kiekkohamsteri.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.dto.MuoviCreateDto;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.v1.PlasticProjection;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.PlasticService;

@RestController
public class MuoviController extends BaseController {
	@Autowired
	AuthService authService;
	@Autowired
	PlasticService plasticService;
	
	@RequestMapping(value="/muovit", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Page<PlasticProjection> getMuovit(@RequestParam(required=false) Long valmId, Pageable pageable, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("MuoviController.getMuovit()...");
		
		Members user = authService.getUser(request);
		if(user == null || user.getLevel() != 2) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return plasticService.getPlastics(valmId, pageable);
		}
	}
	
	@RequestMapping(value="/muovit", method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody
    PlasticProjection createMuovi(@RequestBody MuoviCreateDto dto, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("MuoviController.createMuovi()...");
		
		Members user = authService.getUser(request);
		if(user == null || user.getLevel() != 2) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return plasticService.createPlastic(dto);
		}
	}
}
