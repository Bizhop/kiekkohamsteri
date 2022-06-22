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

import fi.bizhop.kiekkohamsteri.dto.MoldCreateDto;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.v1.MoldProjection;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.MoldService;

@RestController
public class MoldController extends BaseController {
	@Autowired
	AuthService authService;
	@Autowired
	MoldService moldService;
	
	@RequestMapping(value="/molds", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Page<MoldProjection> getMolds(@RequestParam(required=false) Long valmId, Pageable pageable, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("MoldController.getMolds()...");
		
		Members user = authService.getUser(request);
		if(user == null || user.getLevel() != 2) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return moldService.getMolds(valmId, pageable);
		}
	}
	
	@RequestMapping(value="/molds", method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody MoldProjection createMold(@RequestBody MoldCreateDto dto, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("MoldController.createMold()...");
		
		Members user = authService.getUser(request);
		if(user == null || user.getLevel() != 2) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return moldService.createMold(dto);
		}
	}
}
