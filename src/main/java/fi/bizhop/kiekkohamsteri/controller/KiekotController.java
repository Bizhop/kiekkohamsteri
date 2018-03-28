package fi.bizhop.kiekkohamsteri.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.dto.KiekkoDto;
import fi.bizhop.kiekkohamsteri.dto.UploadDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.KiekkoProjection;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.KiekkoService;
import fi.bizhop.kiekkohamsteri.service.UploadService;

@RestController
public class KiekotController extends BaseController {
	@Autowired
	KiekkoService kiekkoService;
	@Autowired
	AuthService authService;
	@Autowired
	UploadService uploadService;
	
	@RequestMapping(value = "/kiekot", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Page<KiekkoProjection> haeKiekot(HttpServletRequest request, HttpServletResponse response, Pageable pageable) {
		LOG.debug("KiekotController.haeKiekot()...");
		
		Members owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return kiekkoService.haeKiekot(owner, pageable);
		}
	}
	
	@RequestMapping(value = "/kiekot/myytavat", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Page<KiekkoProjection> haeMyytavat(HttpServletRequest request, HttpServletResponse response, Pageable pageable) {
		LOG.debug("KiekotController.haeMyytavat()...");
		
		Members owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			return kiekkoService.haeMyytavat(pageable);
		}
	}
	
	@RequestMapping(value = "/kiekot", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody KiekkoProjection uusiKiekko(@RequestBody UploadDto dto, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("KiekotController.uusiKiekko()...");
		
		Members owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			KiekkoProjection kiekko = kiekkoService.uusiKiekko(owner);
			String kuva = String.format("%s-%d", owner.getUsername(), kiekko.getId());
			try {
				uploadService.upload(dto, kuva);
				kiekko = kiekkoService.paivitaKuva(kiekko.getId(), kuva);
				response.setStatus(HttpServletResponse.SC_OK);
				return kiekko;
			} 
			catch (IOException e) {
				try {
					kiekkoService.poistaKiekko(kiekko.getId(), owner);
				} catch (AuthorizationException e1) {
					//pitäisi onnistua aina, ei tehdä mitään
				}
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return null;
			}
		}
	}
	
	@RequestMapping(value = "/kiekot/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public @ResponseBody KiekkoProjection paivitaKiekko(@PathVariable Long id, @RequestBody KiekkoDto dto, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug(String.format("KiekotController.paivitaKiekko(%d)...", id));
		
		Members owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		else {
			try {
				response.setStatus(HttpServletResponse.SC_OK);
				return kiekkoService.paivitaKiekko(dto, id, owner);
			}
			catch(AuthorizationException ae) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return null;
			}
		}
	}
	
	@RequestMapping(value = "/kiekot/{id}", method = RequestMethod.DELETE)
	public void poistaKiekko(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("KiekotController.poistaKiekko()...");
		
		Members owner = authService.getUser(request);
		if(owner == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		else {
			try {
				kiekkoService.poistaKiekko(id, owner);
			}
			catch(AuthorizationException ae) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
		}
	}
}
