package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.controller.BaseControllerV2;
import fi.bizhop.kiekkohamsteri.model.Stats;
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
public class StatsController extends BaseControllerV2 {
	final StatsService statsService;

	@RequestMapping(value="/stats", method=RequestMethod.GET, produces = "application/json")
	public @ResponseBody Page<Stats> getStats(HttpServletResponse response, Pageable pageable) {
		response.setStatus(HttpServletResponse.SC_OK);
		return statsService.getStats(pageable);
	}
}
