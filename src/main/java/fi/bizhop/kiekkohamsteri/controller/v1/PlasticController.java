package fi.bizhop.kiekkohamsteri.controller.v1;

import fi.bizhop.kiekkohamsteri.dto.v2.in.PlasticCreateDto;
import fi.bizhop.kiekkohamsteri.projection.v1.PlasticProjection;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.ManufacturerService;
import fi.bizhop.kiekkohamsteri.service.PlasticService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class PlasticController extends BaseController {
	final AuthService authService;
	final PlasticService plasticService;
	final ManufacturerService manufacturerService;
	
	@RequestMapping(value="/muovit", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Page<PlasticProjection> getPlastics(@RequestParam(required=false) Long valmId, Pageable pageable, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_OK);
		if(valmId == null) return plasticService.getPlastics(pageable);

		return manufacturerService.getManufacturer(valmId)
				.map(m -> plasticService.getPlasticsByManufacturer(m, pageable))
				.orElseGet(() -> {
					LOG.warn("Manufacturer not found, id={}", valmId);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return null;
				});
	}
	
	@RequestMapping(value="/muovit", method=RequestMethod.POST, consumes="application/json")
	public @ResponseBody
    PlasticProjection createMuovi(@RequestBody PlasticCreateDto dto, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_OK);

		return manufacturerService.getManufacturer(dto.getManufacturerId())
				.map(m -> plasticService.createPlastic(dto, m))
				.orElseGet(() -> {
					LOG.warn("Manufacturer not found, id={}", dto.getManufacturerId());
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return null;
				});
	}
}
