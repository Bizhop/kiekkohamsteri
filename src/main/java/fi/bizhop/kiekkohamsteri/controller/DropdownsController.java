package fi.bizhop.kiekkohamsteri.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.bizhop.kiekkohamsteri.dto.DropdownsDto;
import fi.bizhop.kiekkohamsteri.service.DropdownsService;

@RestController
public class DropdownsController extends BaseController {
	@Autowired
	DropdownsService service;
	
	@RequestMapping(value = "/dropdown", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DropdownsDto getDropdowns() {
		return service.getDropdowns();
	}
}
