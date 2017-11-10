package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.R_vari;
import fi.bizhop.kiekkohamsteri.projection.VariDropdownProjection;

public interface VariRepository extends CrudRepository<R_vari, Long> {
	List<VariDropdownProjection> findAllProjectedBy();
}
