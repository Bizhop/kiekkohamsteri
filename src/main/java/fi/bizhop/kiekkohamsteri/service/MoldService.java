package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.ManufacturerRepository;
import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.dto.MoldCreateDto;
import fi.bizhop.kiekkohamsteri.model.R_mold;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.v1.MoldProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MoldService {
	final MoldRepository moldRepo;
	final ManufacturerRepository manufacturerRepo;

	private R_mold DEFAULT_MOLD;

	public Page<MoldProjection> getMolds(Long valmId, Pageable pageable) {
		if(valmId == null) {
			return moldRepo.findAllProjectedBy(pageable);
		}
		else {
			return manufacturerRepo.findById(valmId)
					.map(valm -> moldRepo.findByValmistaja(valm, pageable))
					.orElse(null);
		}
	}

	public MoldProjection createMold(MoldCreateDto dto) {
		R_valm valm = manufacturerRepo.findById(dto.getValmId()).orElseThrow();
		
		R_mold mold = new R_mold();
		mold.setValmistaja(valm);
		BeanUtils.copyProperties(dto, mold, "id", "valmistaja");
		
		R_mold saved = moldRepo.save(mold);

		return moldRepo.getR_moldById(saved.getId());
	}

	public Optional<R_mold> getMold(Long id) {
		return moldRepo.findById(id);
	}

	public R_mold getDefaultMold() {
		if(DEFAULT_MOLD == null) {
			DEFAULT_MOLD = moldRepo.findById(893L).orElseThrow();
		}
		return DEFAULT_MOLD;
	}
}
