package fi.bizhop.kiekkohamsteri.controller;

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
import java.util.Date;
import java.util.List;

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

	@RequestMapping(value = "/kiekot", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Page<DiscProjection> haeKiekot(HttpServletRequest request, HttpServletResponse response, Pageable pageable) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return discService.getDiscs(owner, pageable);
		}
	}
	
	@RequestMapping(value = "/kiekot/myytavat", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Page<DiscProjection> haeMyytavat(HttpServletRequest request, HttpServletResponse response, Pageable pageable) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return discService.getDiscsForSale(pageable);
		}
	}
	
	@RequestMapping(value = "/kiekot", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	DiscProjection uusiKiekko(@RequestBody UploadDto dto, HttpServletRequest request, HttpServletResponse response) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		else {
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
				response.setStatus(HttpServletResponse.SC_OK);
				return disc;
			} 
			catch (IOException e) {
				LOG.error("Cloudinary error uploading image", e);

				//if image upload fails, delete the created disc
				discService.deleteDiscById(disc.getId());
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return null;
			}
		}
	}
	
	@RequestMapping(value = "/kiekot/{id}/update-image", method = RequestMethod.PATCH, produces = "application/json", consumes = "application/json")
	public void paivitaKuva(@PathVariable Long id, @RequestBody UploadDto dto, HttpServletRequest request, HttpServletResponse response) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		else {
			try {
				var kiekko = discService.getDisc(owner, id);
				var kuva = kiekko.getKuva();
				var uusiKuva = kuva + "-" + new Date().getTime();
				if(StringUtils.countOccurrencesOf(kuva, "-") > 1) {
					uusiKuva = kuva.substring(0, kuva.lastIndexOf("-")) + "-" + new Date().getTime();
				}
				uploadService.upload(dto, uusiKuva);
				discService.updateImage(kiekko.getId(), uusiKuva);
				response.setStatus(HttpServletResponse.SC_OK);
			}
			catch (AuthorizationException e) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
			catch (IOException e) {
				LOG.error("Cloudinary error uploading image", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@RequestMapping(value = "/kiekot/{id}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	DiscProjection haeKiekko(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		else {
			try {
				response.setStatus(HttpServletResponse.SC_OK);
				return discService.getDiscIfPublicOrOwn(owner, id);
			}
			catch (AuthorizationException ae) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return null;
			}
		}
	}
	
	@RequestMapping(value = "/kiekot/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public @ResponseBody
	DiscProjection paivitaKiekko(@PathVariable Long id, @RequestBody DiscDto dto, HttpServletRequest request, HttpServletResponse response) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		else {
			try {
				var newMold = moldService.getMold(dto.getMoldId()).orElse(null);
				var newPlastic = plasticService.getPlastic(dto.getMuoviId()).orElse(null);
				var newColor = colorService.getColor(dto.getVariId()).orElse(null);
				response.setStatus(HttpServletResponse.SC_OK);
				return discService.updateDisc(dto, id, owner, newMold, newPlastic, newColor);
			}
			catch(AuthorizationException ae) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return null;
			}
			catch(HttpResponseException hre) {
				LOG.error(hre.getMessage());
				response.setStatus(hre.getStatusCode());
				return null;
			}
			catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return null;
			}
		}
	}
	
	@RequestMapping(value = "/kiekot/{id}", method = RequestMethod.DELETE)
	public void poistaKiekko(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		var owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		else {
			try {
				discService.deleteDisc(id, owner);
				owner.removeDisc();
				userService.saveUser(owner);
			}
			catch(AuthorizationException ae) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
		}
	}
	
	@RequestMapping(value = "/kiekot/{id}/buy", method = RequestMethod.POST)
	public @ResponseBody Ostot ostaKiekko(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		var user = authService.getUser(request);
		if(user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		else {
			try {
				var discOpt = discService.getDiscDb(id);
				if(discOpt.isEmpty()) {
					response.setStatus(HttpServletResponse.SC_NO_CONTENT);
					return null;
				}

				var disc = discOpt.get();

				return buyService.buyDisc(user, disc);
			} catch (HttpResponseException e) {
				LOG.error(e.getMessage());
				response.setStatus(e.getStatusCode());
				return null;
			}
		}
	}
	
	@RequestMapping(value = "/kiekot/public-lists", method = RequestMethod.GET)
	public List<ListingDto> haeJulkisetListat(HttpServletRequest request, HttpServletResponse response, Pageable pageable) {
		var user = authService.getUser(request);
		if(user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		else {
			var usersWithPublicLists = userService.getUsersWithPublicList();
			return discService.getPublicLists(usersWithPublicLists);
		}
	}
	
	@RequestMapping(value = "/kiekot/lost", method = RequestMethod.GET)
	public Page<DiscProjection> getLost(HttpServletRequest request, HttpServletResponse response, Pageable pageable) {
		var user = authService.getUser(request);
		if(user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		else {
			return discService.getLost(pageable);
		}
	}
	
	@RequestMapping(value = "/kiekot/{id}/found", method = RequestMethod.PATCH)
	public void found(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		var user = authService.getUser(request);
		if(user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			try {
				discService.handleFoundDisc(user, id);
			} catch (HttpResponseException e) {
				LOG.error(e.getMessage());
				response.setStatus(e.getStatusCode());
			}
		}
	}
}
