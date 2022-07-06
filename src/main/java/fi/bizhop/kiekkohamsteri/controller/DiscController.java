package fi.bizhop.kiekkohamsteri.controller;

import fi.bizhop.kiekkohamsteri.controller.provider.UUIDProvider;
import fi.bizhop.kiekkohamsteri.dto.DiscDto;
import fi.bizhop.kiekkohamsteri.dto.ListingDto;
import fi.bizhop.kiekkohamsteri.dto.UploadDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.Ostot;
import fi.bizhop.kiekkohamsteri.projection.v1.DiscProjection;
import fi.bizhop.kiekkohamsteri.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
@RequiredArgsConstructor
public class DiscController extends BaseController {
	final DiscService discService;
	final AuthService authService;
	final UploadService uploadService;
	final BuyService buyService;
	final UserService userService;
	final MoldService moldService;
	final PlasticService plasticService;
	final ColorService colorService;
	final UUIDProvider uuidProvider;

	@RequestMapping(value = "/kiekot", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Page<DiscProjection> getDiscs(HttpServletRequest request, HttpServletResponse response, Pageable pageable) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}

		response.setStatus(SC_OK);
		return discService.getDiscs(owner, pageable);
	}

	@RequestMapping(value = "/kiekot/myytavat", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Page<DiscProjection> getDiscsForSale(HttpServletRequest request, HttpServletResponse response, Pageable pageable) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}

		response.setStatus(SC_OK);
		return discService.getDiscsForSale(pageable);
	}

	@RequestMapping(value = "/kiekot", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody DiscProjection createDisc(@RequestBody UploadDto dto, HttpServletRequest request, HttpServletResponse response) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}
		if(invalidUploadDto(dto)) {
			response.setStatus(SC_BAD_REQUEST);
			return null;
		}

		var disc = discService.newDisc(
				owner,
				moldService.getDefaultMold(),
				plasticService.getDefaultPlastic(),
				colorService.getDefaultColor());
		owner.addDisc();
		userService.saveUser(owner);

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
	public void updateImage(@PathVariable Long id, @RequestBody UploadDto dto, HttpServletRequest request, HttpServletResponse response) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return;
		}
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
	public @ResponseBody DiscProjection getDisc(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}

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
	public @ResponseBody DiscProjection updateDisc(@PathVariable Long id, @RequestBody DiscDto dto, HttpServletRequest request, HttpServletResponse response) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}

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
	public void deleteDisc(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return;
		}

		try {
			discService.deleteDisc(id, owner);
			owner.removeDisc();
			userService.saveUser(owner);
			response.setStatus(SC_NO_CONTENT);
		}
		catch(AuthorizationException ae) {
			response.setStatus(SC_FORBIDDEN);
		}
	}

	@RequestMapping(value = "/kiekot/{id}/buy", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Ostot buyDisc(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		var user = authService.getUser(request);
		if(user == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}

		try {
			var disc = discService.getDiscDb(id).orElse(null);

			response.setStatus(SC_OK);
			return buyService.buyDisc(user, disc);
		}
		catch (HttpResponseException e) {
			LOG.error(e.getMessage());
			response.setStatus(e.getStatusCode());
			return null;
		}
	}

	@RequestMapping(value = "/kiekot/public-lists", method = RequestMethod.GET)
	public List<ListingDto> getPublicLists(HttpServletRequest request, HttpServletResponse response, Pageable pageable) {
		var user = authService.getUser(request);
		if(user == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}

		var usersWithPublicLists = userService.getUsersWithPublicList();
		return discService.getPublicLists(usersWithPublicLists);
	}

	@RequestMapping(value = "/kiekot/lost", method = RequestMethod.GET)
	public Page<DiscProjection> getLost(HttpServletRequest request, HttpServletResponse response, Pageable pageable) {
		var user = authService.getUser(request);
		if(user == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return null;
		}

		return discService.getLost(pageable);
	}

	@RequestMapping(value = "/kiekot/{id}/found", method = RequestMethod.PATCH)
	public void markFound(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		var user = authService.getUser(request);
		if(user == null) {
			response.setStatus(SC_UNAUTHORIZED);
			return;
		}

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
