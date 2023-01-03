package fi.bizhop.kiekkohamsteri.controller.v1;

import javax.servlet.http.HttpServletResponse;

import fi.bizhop.kiekkohamsteri.controller.BaseController;
import fi.bizhop.kiekkohamsteri.dto.v1.RoundDto;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.bind.annotation.*;

import fi.bizhop.kiekkohamsteri.dto.v1.RatingDto;
import fi.bizhop.kiekkohamsteri.service.RatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RatingController extends BaseController {
    private static final Logger LOG = LogManager.getLogger(RatingController.class);

	final RatingService ratingService;
	
	@RequestMapping(value = "/rating/{pdga}", method = RequestMethod.GET, produces = "application/json")
	public RatingDto getRounds(@PathVariable String pdga, HttpServletResponse response) {
		try {
			return ratingService.getRounds(pdga);
		} catch (Exception e) {
		    LOG.error("Internat error", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}

	@RequestMapping(value = "/rating", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public Integer getRating(@RequestBody List<RoundDto> rounds, @RequestParam(value = "byRoundsOnly", required = false, defaultValue = "false") boolean byRoundsOnly, HttpServletResponse response) {
	    try {
	        return ratingService.getRating(rounds, false, byRoundsOnly).getNextRating();
	    } catch (Exception e) {
            LOG.error("Internat error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}
