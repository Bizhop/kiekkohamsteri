package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.dto.v2.in.MoldCreateDto;
import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import fi.bizhop.kiekkohamsteri.model.Mold;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoldService {
	private static final Long DEFAULT_MOLD_ID = Utils.getLongFromEnv("DEFAULT_MOLD_ID", 893L);

	final MoldRepository moldRepo;

	private Mold DEFAULT_MOLD;

	public Mold createMold(MoldCreateDto dto, Manufacturer manufacturer) {
		var mold = new Mold();
		mold.setManufacturer(manufacturer);
		BeanUtils.copyProperties(dto, mold, "manufacturerId");
		
		return moldRepo.save(mold);
	}

	public Mold getDefaultMold() {
		if(DEFAULT_MOLD == null) {
			DEFAULT_MOLD = moldRepo.findById(DEFAULT_MOLD_ID).orElseThrow();
		}
		return DEFAULT_MOLD;
	}

	// Passthrough methods to db
	// Not covered (or to be covered by unit tests)

	public Page<Mold> getMolds(Pageable pageable) {
		return moldRepo.findAll(pageable);
	}

	public Page<Mold> getMoldsByManufacturer(Manufacturer manufacturer, Pageable pageable) {
		return moldRepo.findByManufacturer(manufacturer, pageable);
	}

	public Mold getMold(Long id) {
		return moldRepo.findById(id).orElse(null);
	}
}
