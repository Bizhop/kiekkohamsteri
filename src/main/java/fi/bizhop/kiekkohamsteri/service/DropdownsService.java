package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.*;
import fi.bizhop.kiekkohamsteri.dto.DropdownsDto;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DropdownsService {
	final MoldRepository moldRepository;
	final ManufacturerRepository manufacturerRepository;
	final PlasticRepository plasticRepository;
	final ColorRepository colorRepository;
	final DropdownRepository dropdownRepository;

	public DropdownsDto getDropdowns(Long manufacturerId) {
		return DropdownsDto.builder()
				.valms(getManufacturers())
				.molds(getMolds(manufacturerId))
				.muovit(getPlastics(manufacturerId))
				.varit(getColors())
				.kunto(getCondition())
				.tussit(getMarkings())
				.build();
	}

	private List<DropdownProjection> getMarkings() {
		return dropdownRepository.findByValikkoOrderByArvoAsc("tussit");
	}

	private List<DropdownProjection> getCondition() {
		return dropdownRepository.findByValikkoOrderByArvoAsc("kunto");
	}

	private List<MoldDropdownProjection> getMolds(Long valmId) {
		if(valmId == null) {
			return moldRepository.findAllByOrderByKiekkoAsc();
		}
		else {
			var valm = manufacturerRepository.findById(valmId).orElseThrow();
			return moldRepository.findByValmistajaOrderByKiekkoAsc(valm);
		}
	}

	private List<ManufacturerDropdownProjection> getManufacturers() {
		return manufacturerRepository.findAllProjectedBy();
	}
	
	private List<PlasticDropdownProjection> getPlastics(Long valmId) {
		if(valmId == null) {
			return plasticRepository.findAllByOrderByMuoviAsc();
		}
		else {
			var valm = manufacturerRepository.findById(valmId).orElseThrow();
			return plasticRepository.findByValmistajaOrderByMuoviAsc(valm);
		}
	}
	
	private List<ColorDropdownProjection> getColors() {
		return colorRepository.findAllProjectedBy();
	}
}
