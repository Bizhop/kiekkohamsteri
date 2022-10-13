package fi.bizhop.kiekkohamsteri.controller.v1;

import fi.bizhop.kiekkohamsteri.controller.provider.UUIDProvider;
import fi.bizhop.kiekkohamsteri.dto.v1.in.DiscInputDto;
import fi.bizhop.kiekkohamsteri.dto.v1.in.UploadDto;
import fi.bizhop.kiekkohamsteri.dto.v1.out.BuyOutputDto;
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
import java.util.HashSet;
import java.util.NoSuchElementException;

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
	final UUIDProvider uuidProvider;
	final UserService userService;

	@RequestMapping(value = "/kiekot", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Page<DiscProjection> getDiscs(
			@RequestAttribute("user") User me,
			@RequestParam(required = false) Long userId,
			HttpServletResponse response,
			Pageable pageable) {
		if(userId != null) {
			try {
				var otherUser = userService.getUser(userId);
				var groupIntersection = new HashSet<>(me.getGroups());
				groupIntersection.retainAll(otherUser.getGroups());
				if(groupIntersection.isEmpty()) {
					response.setStatus(SC_FORBIDDEN);
					return null;
				}
				return discService.getDiscs(otherUser, pageable);
			} catch (NoSuchElementException e) {
				response.setStatus(SC_BAD_REQUEST);
				return null;
			}
		}

		response.setStatus(SC_OK);
		return discService.getDiscs(me, pageable);
	}

	@RequestMapping(value = "/kiekot/myytavat", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Page<DiscProjection> getDiscsForSale(HttpServletResponse response, Pageable pageable) {
		response.setStatus(SC_OK);
		return discService.getDiscsForSale(pageable);
	}

	@RequestMapping(value = "/kiekot", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody DiscProjection createDisc(@RequestAttribute("user") User owner, @RequestBody UploadDto dto, HttpServletResponse response) {
		if(invalidUploadDto(dto)) {
			response.setStatus(SC_BAD_REQUEST);
			return null;
		}

		var disc = discService.newDisc(
				owner,
				moldService.getDefaultMold(),
				plasticService.getDefaultPlastic(),
				colorService.getDefaultColor());

		var image = String.format("%s-%d", owner.getUsername(), disc.getId());
		try {
			uploadService.upload(dto, image);
			disc = discService.updateImage(disc.getId(), image);
			response.setStatus(SC_OK);
			return disc;
		}
		catch (IOException e) {
			LOG.error("Cloudinary error uploading image", e);

			//if image upload fails, delete the created disc
			discService.deleteDiscById(disc.getId());
			response.setStatus(SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}

	@RequestMapping(value = "/kiekot/{id}/update-image", method = RequestMethod.PATCH, produces = "application/json", consumes = "application/json")
	public void updateImage(@RequestAttribute("user") User owner, @PathVariable Long id, @RequestBody UploadDto dto, HttpServletResponse response) {
		if(invalidUploadDto(dto)) {
			response.setStatus(SC_BAD_REQUEST);
			return;
		}

		try {
			var disc = discService.getDisc(owner, id);
			var image = disc.getKuva();
			//if image name already has more than one "-", it has been updated previously
			// then replace the uuid with new one
			var uuid = uuidProvider.getUuid();
			var newImage = StringUtils.countOccurrencesOf(image, "-") > 1
					? image.substring(0, image.lastIndexOf("-")) + "-" + uuid
					: image + "-" + uuid;

			uploadService.upload(dto, newImage);
			discService.updateImage(disc.getId(), newImage);
			response.setStatus(SC_NO_CONTENT);
		}
		catch (AuthorizationException e) {
			LOG.error("{} trying to update someone else's disc", owner.getEmail());
			response.setStatus(SC_FORBIDDEN);
		}
		catch (IOException e) {
			LOG.error("Cloudinary error uploading image", e);
			response.setStatus(SC_INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/kiekot/{id}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DiscProjection getDisc(@RequestAttribute("user") User owner, @PathVariable Long id, HttpServletResponse response) {
		try {
			response.setStatus(SC_OK);
			return discService.getDiscIfPublicOrOwn(owner, id);
		}
		catch (AuthorizationException ae) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}
	}

	@RequestMapping(value = "/kiekot/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public @ResponseBody DiscProjection updateDisc(@RequestAttribute("user") User owner, @PathVariable Long id, @RequestBody DiscInputDto dto, HttpServletResponse response) {
		try {
			var newMold = moldService.getMold(dto.getMoldId()).orElse(null);
			var newPlastic = plasticService.getPlastic(dto.getMuoviId()).orElse(null);
			var newColor = colorService.getColor(dto.getVariId()).orElse(null);
			response.setStatus(SC_OK);
			return discService.updateDisc(dto, id, owner, newMold, newPlastic, newColor);
		}
		catch(AuthorizationException ae) {
			response.setStatus(SC_FORBIDDEN);
			return null;
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.setStatus(SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}

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
