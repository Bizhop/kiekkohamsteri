package fi.bizhop.kiekkohamsteri.controller.v1;

import fi.bizhop.kiekkohamsteri.dto.v2.in.UploadDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.BuyOutputDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.projection.v1.DiscProjection;
import fi.bizhop.kiekkohamsteri.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
@RequiredArgsConstructor
public class DiscController extends BaseController {
	final DiscService discService;
	final AuthService authService;
	final UploadService uploadService;
	final BuyService buyService;
	final MoldService moldService;
	final PlasticService plasticService;
	final ColorService colorService;
	final Clock clock;
	final UserService userService;

	@RequestMapping(value = "/kiekot/{id}", method = RequestMethod.DELETE)
	public void deleteDisc(@RequestAttribute("user") User owner, @PathVariable Long id, HttpServletResponse response) {
		try {
			discService.deleteDisc(id, owner);
			response.setStatus(SC_NO_CONTENT);
		}
		catch(AuthorizationException ae) {
			response.setStatus(SC_FORBIDDEN);
		}
	}

	@RequestMapping(value = "/kiekot/{id}/buy", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody BuyOutputDto buyDisc(@RequestAttribute("user") User user, @PathVariable Long id, HttpServletResponse response) {
		try {
			var disc = discService.getDiscDb(id).orElse(null);

			response.setStatus(SC_OK);
			var buy = buyService.buyDisc(user, disc);
			return BuyOutputDto.fromDb(buy);
		}
		catch (HttpResponseException e) {
			LOG.error(e.getMessage());
			response.setStatus(e.getStatusCode());
			return null;
		}
	}

	@RequestMapping(value = "/kiekot/lost", method = RequestMethod.GET)
	public Page<DiscProjection> getLost(Pageable pageable) {
		return discService.getLost(pageable);
	}

	@RequestMapping(value = "/kiekot/{id}/found", method = RequestMethod.PATCH)
	public void markFound(@RequestAttribute("user") User user, @PathVariable Long id, HttpServletResponse response) {
		try {
			discService.handleFoundDisc(user, id);
			response.setStatus(SC_NO_CONTENT);
		} catch (HttpResponseException e) {
			LOG.error(e.getMessage());
			response.setStatus(e.getStatusCode());
		}
	}

	private boolean invalidUploadDto(UploadDto dto) {
		return dto.getData() == null;
	}
}
