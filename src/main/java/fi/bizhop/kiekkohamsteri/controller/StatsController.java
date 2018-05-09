package fi.bizhop.kiekkohamsteri.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.model.Stats;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.StatsService;

@RestController
public class StatsController extends BaseController {
	@Autowired
	StatsService statsService;
	@Autowired
	AuthService authService;
	
	@RequestMapping(value="/stats", method=RequestMethod.GET, produces = "application/json")
	public @ResponseBody Page<Stats> getStats(HttpServletRequest request, HttpServletResponse response, Pageable pageable) {
		LOG.info("StatsController.getStats()");
		
		Members user = authService.getUser(request);
		if(user == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return statsService.getStats(pageable);
		}
	}
}
