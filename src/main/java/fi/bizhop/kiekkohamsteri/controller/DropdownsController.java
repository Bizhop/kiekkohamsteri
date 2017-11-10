package fi.bizhop.kiekkohamsteri.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.dto.DropdownsDto;
import fi.bizhop.kiekkohamsteri.projection.MoldDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.ValmDropdownProjection;
import fi.bizhop.kiekkohamsteri.service.DropdownsService;

@RestController
public class DropdownsController extends BaseController {
	@Autowired
	DropdownsService service;
	
	@RequestMapping(value = "/dropdown/mold", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<MoldDropdownProjection> getMolds(@RequestParam(required = false) Long valmistajaId) {
		return service.getMolds(valmistajaId);
	}
	
	@RequestMapping(value = "/dropdown/valm", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<ValmDropdownProjection> getValms() {
		return service.getValms();
	}
	
	@RequestMapping(value = "/dropdown", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DropdownsDto getDropdowns() {
		return service.getDropdowns();
	}
}
