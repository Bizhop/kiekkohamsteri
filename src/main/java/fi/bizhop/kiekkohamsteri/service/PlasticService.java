package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.PlasticRepository;
import fi.bizhop.kiekkohamsteri.dto.v2.in.PlasticCreateDto;
import fi.bizhop.kiekkohamsteri.model.Plastic;
import fi.bizhop.kiekkohamsteri.model.Manufacturer;
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
	private Plastic DEFAULT_PLASTIC;

	public PlasticProjection createPlastic(PlasticCreateDto dto, Manufacturer manufacturer) {
		var plastic = new Plastic();
		plastic.setManufacturer(manufacturer);
		plastic.setName(dto.getName());
		
		var saved = plasticRepository.save(plastic);
		return plasticRepository.getPlasticById(saved.getId());
	}

    public Plastic getDefaultPlastic() {
		if(DEFAULT_PLASTIC == null) {
			DEFAULT_PLASTIC = plasticRepository.findById(DEFAULT_PLASTIC_ID).orElseThrow();
		}
		return DEFAULT_PLASTIC;
    }

	// Passthrough methods to db
	// Not covered (or to be covered by unit tests)

	public Optional<Plastic> getPlastic(long id) {
		return plasticRepository.findById(id);
	}

	public Page<PlasticProjection> getPlastics(Pageable pageable) {
		return plasticRepository.findAllProjectedBy(pageable);
	}

	public Page<PlasticProjection> getPlasticsByManufacturer(Manufacturer manufacturer, Pageable pageable) {
		return plasticRepository.findByManufacturer(manufacturer, pageable);
	}
}
