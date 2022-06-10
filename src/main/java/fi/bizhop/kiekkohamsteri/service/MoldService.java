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
			return valmRepo.findById(valmId)
					.map(valm -> moldRepo.findByValmistaja(valm, pageable))
					.orElse(null);
		}
	}

	public MoldProjection createMold(MoldCreateDto dto) {
		R_valm valm = valmRepo.findById(dto.getValmId()).orElseThrow();
		
		R_mold mold = new R_mold();
		mold.setValmistaja(valm);
		BeanUtils.copyProperties(dto, mold, "id", "valmistaja");
		
		R_mold saved = moldRepo.save(mold);

		return moldRepo.getR_moldById(saved.getId());
	}
}
