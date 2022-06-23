package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.dto.MoldCreateDto;
import fi.bizhop.kiekkohamsteri.model.R_mold;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.v1.MoldProjection;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MoldService {
	private static final Long DEFAULT_MOLD_ID = Utils.getLongFromEnv("DEFAULT_MOLD_ID", 893L);

	final MoldRepository moldRepo;

	private R_mold DEFAULT_MOLD;

	public MoldProjection createMold(MoldCreateDto dto, R_valm manufacturer) {
		var mold = new R_mold();
		mold.setValmistaja(manufacturer);
		BeanUtils.copyProperties(dto, mold, "id", "valmistaja");
		
		var saved = moldRepo.save(mold);
		return moldRepo.getR_moldById(saved.getId());
	}

	public R_mold getDefaultMold() {
		if(DEFAULT_MOLD == null) {
			DEFAULT_MOLD = moldRepo.findById(DEFAULT_MOLD_ID).orElseThrow();
		}
		return DEFAULT_MOLD;
	}

	// Passthrough methods to db
	// Not covered (or to be covered by unit tests)

	public Page<MoldProjection> getMolds(Pageable pageable) {
		return moldRepo.findAllProjectedBy(pageable);
	}

	public Page<MoldProjection> getMoldsByManufacturer(R_valm manufacturer, Pageable pageable) {
		return moldRepo.findByValmistaja(manufacturer, pageable);
	}

	public Optional<R_mold> getMold(Long id) {
		return moldRepo.findById(id);
	}
}
