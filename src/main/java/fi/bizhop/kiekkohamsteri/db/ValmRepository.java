package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.ValmDropdownProjection;

public interface ValmRepository extends CrudRepository<R_valm, Long> {
	List<ValmDropdownProjection> findAllProjectedBy();

	R_valm findFirstByValmistaja(String valmistaja);
}
