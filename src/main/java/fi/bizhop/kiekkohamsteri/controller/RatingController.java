package fi.bizhop.kiekkohamsteri.controller;

import javax.servlet.http.HttpServletResponse;

import fi.bizhop.kiekkohamsteri.dto.RoundDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import fi.bizhop.kiekkohamsteri.dto.RatingDto;
import fi.bizhop.kiekkohamsteri.service.RatingService;

import java.util.List;

@RestController
public class RatingController extends BaseController {
	@Autowired
	RatingService ratingService;
	
	@RequestMapping(value = "/rating/{pdga}", method = RequestMethod.GET, produces = "application/json")
	public RatingDto getRounds(@PathVariable String pdga, HttpServletResponse response) {
		try {
			return ratingService.getRounds(pdga);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}

	@RequestMapping(value = "/rating", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public Integer getRating(@RequestBody List<RoundDto> rounds, HttpServletResponse response) {
	    try {
	        return ratingService.getRating(rounds, false).getNextRating();
	    } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}
