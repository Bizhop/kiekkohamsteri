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
	final MoldRepository moldRepo;
	final ManufacturerRepository valmRepo;
	final PlasticRepository muoviRepo;
	final ColorRepository variRepo;
	final DropdownRepository ddRepo;

	public DropdownsDto getDropdowns(Long valmId) {
		var dto = new DropdownsDto();
		dto.setValms(getValms())
			.setMolds(getMolds(valmId))
			.setMuovit(getMuovit(valmId))
			.setVarit(getVarit())
			.setKunto(getKunto())
			.setTussit(getTussit());
		return dto;
	}

	private List<DropdownProjection> getTussit() {
		return ddRepo.findByValikkoOrderByArvoAsc("tussit");
	}

	private List<DropdownProjection> getKunto() {
		return ddRepo.findByValikkoOrderByArvoAsc("kunto");
	}

	private List<MoldDropdownProjection> getMolds(Long valmId) {
		if(valmId == null) {
			return moldRepo.findAllByOrderByKiekkoAsc();
		}
		else {
			var valm = valmRepo.findById(valmId).orElseThrow();
			return moldRepo.findByValmistajaOrderByKiekkoAsc(valm);
		}
	}

	private List<ManufacturerDropdownProjection> getValms() {
		return valmRepo.findAllProjectedBy();
	}
	
	private List<PlasticDropdownProjection> getMuovit(Long valmId) {
		if(valmId == null) {
			return muoviRepo.findAllByOrderByMuoviAsc();
		}
		else {
			var valm = valmRepo.findById(valmId).orElseThrow();
			return muoviRepo.findByValmistajaOrderByMuoviAsc(valm);
		}
	}
	
	private List<ColorDropdownProjection> getVarit() {
		return variRepo.findAllProjectedBy();
	}
}
