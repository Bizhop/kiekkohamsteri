package fi.bizhop.kiekkohamsteri.controller.v1;

import javax.servlet.http.HttpServletResponse;

import fi.bizhop.kiekkohamsteri.dto.v2.in.MoldCreateDto;
import fi.bizhop.kiekkohamsteri.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import fi.bizhop.kiekkohamsteri.projection.v1.MoldProjection;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.MoldService;

@RestController
@RequiredArgsConstructor
public class MoldController extends BaseController {
	final AuthService authService;
	final MoldService moldService;
	final ManufacturerService manufacturerService;
	
	@RequestMapping(value="/molds", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Page<MoldProjection> getMolds(@RequestParam(required=false) Long valmId, Pageable pageable, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_OK);
		if(valmId == null) return moldService.getMolds(pageable);

		return manufacturerService.getManufacturer(valmId)
				.map(m -> moldService.getMoldsByManufacturer(m, pageable))
				.orElseGet(() -> {
					LOG.warn("Manufacturer not found, id={}", valmId);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return null;
				});
	}
	
	@RequestMapping(value="/molds", method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody MoldProjection createMold(@RequestBody MoldCreateDto dto, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_OK);

		return manufacturerService.getManufacturer(dto.getManufacturerId())
				.map(m -> moldService.createMold(dto, m))
				.orElseGet(() -> {
					LOG.warn("Manufacturer not found, id={}", dto.getManufacturerId());
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return null;
				});
	}
}
