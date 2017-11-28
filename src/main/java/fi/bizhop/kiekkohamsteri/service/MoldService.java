package fi.bizhop.kiekkohamsteri.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.db.ValmRepository;
import fi.bizhop.kiekkohamsteri.dto.MoldCreateDto;
import fi.bizhop.kiekkohamsteri.model.R_mold;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.MoldProjection;

@Service
public class MoldService {
	@Autowired
	MoldRepository moldRepo;
	@Autowired
	ValmRepository valmRepo;
	
	public Page<MoldProjection> getMolds(Long valmId, Pageable pageable) {
		if(valmId == null) {
			return moldRepo.findAllProjectedBy(pageable);
		}
		else {
			R_valm valm = valmRepo.findOne(valmId);
			if(valm == null) {
				return null;
			}
			else {
				return moldRepo.findByValmistaja(valm, pageable);
			}
		}
	}

	public void createMold(MoldCreateDto dto) {
		R_valm valm = valmRepo.findOne(dto.getValmId());
		
		R_mold mold = new R_mold();
		mold.setValmistaja(valm);
		BeanUtils.copyProperties(dto, mold, "id", "valmistaja");
		
		moldRepo.save(mold);
	}
}
