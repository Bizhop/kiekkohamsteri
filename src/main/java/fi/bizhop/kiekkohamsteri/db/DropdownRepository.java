package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.DDArvot;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.DropdownProjection;

public interface DropdownRepository extends CrudRepository<DDArvot, Long> {
	List<DropdownProjection> findByValikkoOrderByArvoAsc(String valikko);
}
