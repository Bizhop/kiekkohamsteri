package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.*;
import fi.bizhop.kiekkohamsteri.dto.v2.out.DropdownsDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.DropdownOutputDto;
import fi.bizhop.kiekkohamsteri.model.Mold;
import fi.bizhop.kiekkohamsteri.model.Plastic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
				.manufacturers(getManufacturers())
				.molds(getMolds(manufacturerId))
				.plastics(getPlastics(manufacturerId))
				.colors(getColors())
				.conditions(getCondition())
				.markings(getMarkings())
				.build();
	}

	private List<DropdownOutputDto> getMarkings() {
		return dropdownRepository.findByMenuOrderByValueAsc("tussit").stream()
				.map(DropdownOutputDto::fromDropdownInterface)
				.collect(Collectors.toList());
	}

	private List<DropdownOutputDto> getCondition() {
		return dropdownRepository.findByMenuOrderByValueAsc("kunto").stream()
				.map(DropdownOutputDto::fromDropdownInterface)
				.collect(Collectors.toList());
	}

	private List<DropdownOutputDto> getMolds(Long manufacturerId) {
		if(manufacturerId == null) return mapMoldsToDto(moldRepository.findAllByOrderByNameAsc());

		var manufacturer = manufacturerRepository.findById(manufacturerId).orElseThrow();
		return mapMoldsToDto(moldRepository.findByManufacturerOrderByNameAsc(manufacturer));
	}

	private List<DropdownOutputDto> getManufacturers() {
		return manufacturerRepository.findAll().stream()
				.map(DropdownOutputDto::fromDropdownInterface)
				.collect(Collectors.toList());
	}
	
	private List<DropdownOutputDto> getPlastics(Long manufacturerId) {
		if(manufacturerId == null) return mapPlasticsToDto(plasticRepository.findAllByOrderByNameAsc());

		var manufacturer = manufacturerRepository.findById(manufacturerId).orElseThrow();
		return mapPlasticsToDto(plasticRepository.findByManufacturerOrderByNameAsc(manufacturer));
	}
	
	private List<DropdownOutputDto> getColors() {
		return colorRepository.findAll().stream()
				.map(DropdownOutputDto::fromDropdownInterface)
				.collect(Collectors.toList());
	}

	private static List<DropdownOutputDto> mapMoldsToDto(List<Mold> input) {
		return input.stream()
				.map(DropdownOutputDto::fromDropdownInterface)
				.collect(Collectors.toList());
	}

	private static List<DropdownOutputDto> mapPlasticsToDto(List<Plastic> input) {
		return input.stream()
				.map(DropdownOutputDto::fromDropdownInterface)
				.collect(Collectors.toList());
	}
}
