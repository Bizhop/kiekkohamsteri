package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.ManufacturerRepository;
import fi.bizhop.kiekkohamsteri.db.PlasticRepository;
import fi.bizhop.kiekkohamsteri.dto.MuoviCreateDto;
import fi.bizhop.kiekkohamsteri.model.R_muovi;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.v1.PlasticProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlasticService {
	final PlasticRepository plasticRepository;
	final ManufacturerRepository manufacturerRepository;

	private R_muovi DEFAULT_PLASTIC;
	
	public Page<PlasticProjection> getPlastics(Long valmId, Pageable pageable) {
		if(valmId == null) {
			return plasticRepository.findAllProjectedBy(pageable);
		}
		else {
			R_valm valm = manufacturerRepository.findById(valmId).orElse(null);
			if(valm == null) {
				return null;
			}
			else {
				return plasticRepository.findByValmistaja(valm, pageable);
			}
		}
	}

	public PlasticProjection createPlastic(MuoviCreateDto dto) {
		R_valm valm = manufacturerRepository.findById(dto.getValmId()).orElseThrow();
		
		R_muovi muovi = new R_muovi();
		muovi.setValmistaja(valm);
		muovi.setMuovi(dto.getMuovi());
		
		R_muovi saved = plasticRepository.save(muovi);

		return plasticRepository.getR_muoviById(saved.getId());
	}

	public Optional<R_muovi> getPlastic(long id) {
		return plasticRepository.findById(id);
	}

    public R_muovi getDefaultPlastic() {
		if(DEFAULT_PLASTIC == null) {
			DEFAULT_PLASTIC = plasticRepository.findById(13L).orElseThrow();
		}
		return DEFAULT_PLASTIC;
    }
}
