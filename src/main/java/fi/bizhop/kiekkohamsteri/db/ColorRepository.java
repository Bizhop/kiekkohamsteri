package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.Color;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.ColorDropdownProjection;

public interface ColorRepository extends CrudRepository<Color, Long> {
	List<ColorDropdownProjection> findAllProjectedBy();
}
