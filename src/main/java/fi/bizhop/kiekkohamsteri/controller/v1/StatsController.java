package fi.bizhop.kiekkohamsteri.controller.v1;

import fi.bizhop.kiekkohamsteri.model.Stats;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class StatsController extends BaseController {
	final StatsService statsService;
	final AuthService authService;
	
	@RequestMapping(value="/stats", method=RequestMethod.GET, produces = "application/json")
	public @ResponseBody Page<Stats> getStats(HttpServletResponse response, Pageable pageable) {
		response.setStatus(HttpServletResponse.SC_OK);
		return statsService.getStats(pageable);
	}
}
