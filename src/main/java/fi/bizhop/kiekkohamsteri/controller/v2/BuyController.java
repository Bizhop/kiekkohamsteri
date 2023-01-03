package fi.bizhop.kiekkohamsteri.controller.v2;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import fi.bizhop.kiekkohamsteri.controller.BaseControllerV2;
import fi.bizhop.kiekkohamsteri.dto.v2.out.BuyOutputDto;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.service.DiscService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import fi.bizhop.kiekkohamsteri.dto.v2.out.BuySummaryDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.model.Buy.Status;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.BuyService;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
@RequiredArgsConstructor
public class BuyController extends BaseControllerV2 {
	final BuyService buyService;
	final AuthService authService;
	final DiscService discService;

	@RequestMapping(value = "/buys", method = RequestMethod.GET)
	public @ResponseBody List<BuyOutputDto> listing(@RequestParam(value = "status", required = false) Status status, HttpServletResponse response ) {
		response.setStatus(SC_OK);
		var buys = buyService.getListing(status);
		return buys.stream().map(BuyOutputDto::fromDb).collect(Collectors.toList());
	}

	@RequestMapping(value = "/buys/own", method = RequestMethod.GET)
	public @ResponseBody BuySummaryDto summary(@RequestAttribute("user") User user, HttpServletResponse response) {
		response.setStatus(SC_OK);
		return buyService.getSummary(user);
	}

	@RequestMapping(value = "/buys/{id}/confirm", method = RequestMethod.POST)
	public void confirm(@PathVariable Long id, @RequestAttribute("user") User user, HttpServletResponse response) {
		try {
			response.setStatus(SC_OK);
			var disc = buyService.confirm(id, user);
			discService.saveDisc(disc);
		}
		catch (AuthorizationException e) {
			response.setStatus(SC_FORBIDDEN);
		}
	}

	@RequestMapping(value = "/buys/{id}/reject", method = RequestMethod.POST)
	public void reject(@PathVariable Long id, @RequestAttribute("user") User user, HttpServletResponse response) {
		try {
			response.setStatus(SC_OK);
			buyService.reject(id, user);
		}
		catch (AuthorizationException e) {
			response.setStatus(SC_FORBIDDEN);
		}
	}
}