package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.DropdownValues;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.DropdownProjection;

public interface DropdownRepository extends CrudRepository<DropdownValues, Long> {
	List<DropdownProjection> findByMenuOrderByValueAsc(String menu);
}
