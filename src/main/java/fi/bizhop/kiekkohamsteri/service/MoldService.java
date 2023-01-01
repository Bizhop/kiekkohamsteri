package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.dto.v2.in.MoldCreateDto;
import fi.bizhop.kiekkohamsteri.model.Mold;
import fi.bizhop.kiekkohamsteri.model.Manufacturer;
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

	private Mold DEFAULT_MOLD;

	public MoldProjection createMold(MoldCreateDto dto, Manufacturer manufacturer) {
		var mold = new Mold();
		mold.setManufacturer(manufacturer);
		BeanUtils.copyProperties(dto, mold, "id", "manufacturer");
		
		var saved = moldRepo.save(mold);
		return moldRepo.getMoldById(saved.getId());
	}

	public Mold getDefaultMold() {
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

	public Page<MoldProjection> getMoldsByManufacturer(Manufacturer manufacturer, Pageable pageable) {
		return moldRepo.findByManufacturer(manufacturer, pageable);
	}

	public Optional<Mold> getMold(Long id) {
		return moldRepo.findById(id);
	}
}
