package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.R_muovi;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.MuoviDropdownProjection;

public interface MuoviRepository extends CrudRepository<R_muovi, Long> {
	List<MuoviDropdownProjection> findAllByOrderByMuoviAsc();
	List<MuoviDropdownProjection> findByValmistajaOrderByMuoviAsc(R_valm valm);
}