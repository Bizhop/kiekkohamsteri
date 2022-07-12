package fi.bizhop.kiekkohamsteri.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.bizhop.kiekkohamsteri.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import fi.bizhop.kiekkohamsteri.dto.MoldCreateDto;
import fi.bizhop.kiekkohamsteri.model.Members;
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

		return manufacturerService.getManufacturer(dto.getValmId())
				.map(m -> moldService.createMold(dto, m))
				.orElseGet(() -> {
					LOG.warn("Manufacturer not found, id={}", dto.getValmId());
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return null;
				});
	}
}
