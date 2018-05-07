package fi.bizhop.kiekkohamsteri.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@RequestMapping(value="/stats/generate", method = RequestMethod.POST)
	public void generateStats(HttpServletRequest request, HttpServletResponse response) {
		LOG.info("StatsController.generateStats()");
		
		if(statsService.generateStats()) {
			response.setStatus(HttpServletResponse.SC_OK);
		}
		else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="/stats/generate/{year}/{month}", method = RequestMethod.POST)
	public void generateStatsByYearAndMonth(@PathVariable int year, @PathVariable int month, HttpServletRequest request, HttpServletResponse response) {
		LOG.info(String.format("StatsController.generateStatsByYearAndMonth(%d,%d)", year, month));
		
		if(statsService.generateStatsByYearAndMonth(year, month)) {
			response.setStatus(HttpServletResponse.SC_OK);
		}
		else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="/stats", method=RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Stats> getStats(HttpServletRequest request, HttpServletResponse response) {
		LOG.info("StatsController.getStats()");
		
		Members user = authService.getUser(request);
		if(user == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return statsService.getStats();
		}
	}
}
