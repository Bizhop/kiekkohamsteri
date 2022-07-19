package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.*;
import fi.bizhop.kiekkohamsteri.dto.v1.out.DropdownsDto;
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
		return dropdownRepository.findByMenuOrderByValueAsc("tussit");
	}

	private List<DropdownProjection> getCondition() {
		return dropdownRepository.findByMenuOrderByValueAsc("kunto");
	}

	private List<MoldDropdownProjection> getMolds(Long manufacturerId) {
		if(manufacturerId == null) return moldRepository.findAllByOrderByNameAsc();

		var manufacturer = manufacturerRepository.findById(manufacturerId).orElseThrow();
		return moldRepository.findByManufacturerOrderByNameAsc(manufacturer);
	}

	private List<ManufacturerDropdownProjection> getManufacturers() {
		return manufacturerRepository.findAllProjectedBy();
	}
	
	private List<PlasticDropdownProjection> getPlastics(Long manufacturerId) {
		if(manufacturerId == null) return plasticRepository.findAllByOrderByNameAsc();

		var manufacturer = manufacturerRepository.findById(manufacturerId).orElseThrow();
		return plasticRepository.findByManufacturerOrderByNameAsc(manufacturer);
	}
	
	private List<ColorDropdownProjection> getColors() {
		return colorRepository.findAllProjectedBy();
	}
}
