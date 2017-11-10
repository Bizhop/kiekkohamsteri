package fi.bizhop.kiekkohamsteri.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.db.MuoviRepository;
import fi.bizhop.kiekkohamsteri.db.ValmRepository;
import fi.bizhop.kiekkohamsteri.db.VariRepository;
import fi.bizhop.kiekkohamsteri.dto.DropdownsDto;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.MoldDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.MuoviDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.ValmDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.VariDropdownProjection;

@Service
public class DropdownsService {
	@Autowired
	MoldRepository moldRepo;
	@Autowired
	ValmRepository valmRepo;
	@Autowired
	MuoviRepository muoviRepo;
	@Autowired
	VariRepository variRepo;
	
	public DropdownsDto getDropdowns() {
		DropdownsDto dto = new DropdownsDto();
		dto.setValms(getValms()).setMolds(getMolds(null)).setMuovit(getMuovit()).setVarit(getVarit());
		return dto;
	}

	private List<MoldDropdownProjection> getMolds(Long valmistajaId) {
		if(valmistajaId == null) {
			return moldRepo.findAllByOrderByKiekkoAsc();
		}
		else {
			R_valm valm = valmRepo.findOne(valmistajaId);
			return moldRepo.findByValmistajaOrderByKiekkoAsc(valm);
		}
	}

	private List<ValmDropdownProjection> getValms() {
		return valmRepo.findAllProjectedBy();
	}
	
	private List<MuoviDropdownProjection> getMuovit() {
		return muoviRepo.findAllProjectedBy();
	}
	
	private List<VariDropdownProjection> getVarit() {
		return variRepo.findAllProjectedBy();
	}
}
