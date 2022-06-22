package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.R_vari;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.ColorDropdownProjection;

public interface ColorRepository extends CrudRepository<R_vari, Long> {
	List<ColorDropdownProjection> findAllProjectedBy();
}
