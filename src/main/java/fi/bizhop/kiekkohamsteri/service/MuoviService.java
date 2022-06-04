package fi.bizhop.kiekkohamsteri.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.MuoviRepository;
import fi.bizhop.kiekkohamsteri.db.ValmRepository;
import fi.bizhop.kiekkohamsteri.dto.MuoviCreateDto;
import fi.bizhop.kiekkohamsteri.model.R_muovi;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.MuoviProjection;

@Service
public class MuoviService {
	@Autowired
	MuoviRepository muoviRepo;
	@Autowired
	ValmRepository valmRepo;
	
	public Page<MuoviProjection> getMuovit(Long valmId, Pageable pageable) {
		if(valmId == null) {
			return muoviRepo.findAllProjectedBy(pageable);
		}
		else {
			R_valm valm = valmRepo.findById(valmId).orElse(null);
			if(valm == null) {
				return null;
			}
			else {
				return muoviRepo.findByValmistaja(valm, pageable);
			}
		}
	}

	public void createMuovi(MuoviCreateDto dto) {
		R_valm valm = valmRepo.findById(dto.getValmId()).orElseThrow();
		
		R_muovi muovi = new R_muovi();
		muovi.setValmistaja(valm);
		muovi.setMuovi(dto.getMuovi());
		
		muoviRepo.save(muovi);
	}
}
