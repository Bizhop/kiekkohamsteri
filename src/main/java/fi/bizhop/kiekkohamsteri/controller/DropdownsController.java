package fi.bizhop.kiekkohamsteri.controller;

import fi.bizhop.kiekkohamsteri.dto.DropdownsDto;
import fi.bizhop.kiekkohamsteri.service.DropdownsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DropdownsController extends BaseController {
	final DropdownsService service;
	
	@RequestMapping(value = "/dropdown", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DropdownsDto getDropdowns(@RequestParam(required=false) Long valmId) {
		return service.getDropdowns(valmId);
	}
}
