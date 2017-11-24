package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.R_mold;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.MoldDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.MoldProjection;

public interface MoldRepository extends CrudRepository<R_mold, Long> {
	List<MoldDropdownProjection> findAllByOrderByKiekkoAsc();
	List<MoldDropdownProjection> findByValmistajaOrderByKiekkoAsc(R_valm valmistaja);
	
	List<MoldProjection> findAllProjectedBy();
}