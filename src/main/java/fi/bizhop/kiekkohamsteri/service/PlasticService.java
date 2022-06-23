package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.PlasticRepository;
import fi.bizhop.kiekkohamsteri.dto.PlasticCreateDto;
import fi.bizhop.kiekkohamsteri.model.R_muovi;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.v1.PlasticProjection;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlasticService {
	final PlasticRepository plasticRepository;

	private static final Long DEFAULT_PLASTIC_ID = Utils.getLongFromEnv("DEFAULT_PLASTIC_ID", 13L);
	private R_muovi DEFAULT_PLASTIC;

	public PlasticProjection createPlastic(PlasticCreateDto dto, R_valm manufacturer) {
		var plastic = new R_muovi();
		plastic.setValmistaja(manufacturer);
		plastic.setMuovi(dto.getMuovi());
		
		var saved = plasticRepository.save(plastic);
		return plasticRepository.getR_muoviById(saved.getId());
	}

    public R_muovi getDefaultPlastic() {
		if(DEFAULT_PLASTIC == null) {
			DEFAULT_PLASTIC = plasticRepository.findById(DEFAULT_PLASTIC_ID).orElseThrow();
		}
		return DEFAULT_PLASTIC;
    }

	// Passthrough methods to db
	// Not covered (or to be covered by unit tests)

	public Optional<R_muovi> getPlastic(long id) {
		return plasticRepository.findById(id);
	}

	public Page<PlasticProjection> getPlastics(Pageable pageable) {
		return plasticRepository.findAllProjectedBy(pageable);
	}

	public Page<PlasticProjection> getPlasticsByManufacturer(R_valm manufacturer, Pageable pageable) {
		return plasticRepository.findByValmistaja(manufacturer, pageable);
	}
}
