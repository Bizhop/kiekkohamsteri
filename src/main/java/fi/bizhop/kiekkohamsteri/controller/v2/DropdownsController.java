package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.controller.BaseControllerV2;
import fi.bizhop.kiekkohamsteri.dto.v2.out.DropdownsDto;
import fi.bizhop.kiekkohamsteri.service.DropdownsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DropdownsController extends BaseControllerV2 {
	final DropdownsService service;
	
	@RequestMapping(value = "/dropdowns", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DropdownsDto getDropdowns(@RequestParam(required=false) Long manufacturerId) {
		return service.getDropdowns(manufacturerId);
	}
}
