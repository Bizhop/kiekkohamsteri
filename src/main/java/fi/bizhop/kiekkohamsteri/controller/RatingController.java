package fi.bizhop.kiekkohamsteri.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.dto.RatingDto;
import fi.bizhop.kiekkohamsteri.service.RatingService;

@RestController
public class RatingController extends BaseController {
	@Autowired
	RatingService ratingService;
	
	@RequestMapping(value = "/rating/{pdga}/rounds", method = RequestMethod.GET)
	public RatingDto getRounds(@PathVariable String pdga, HttpServletResponse response) {
		try {
			return ratingService.getRounds(pdga);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
}
