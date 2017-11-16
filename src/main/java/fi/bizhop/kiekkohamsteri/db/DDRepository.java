package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.DDArvot;
import fi.bizhop.kiekkohamsteri.projection.DDProjection;

public interface DDRepository extends CrudRepository<DDArvot, Long> {
	List<DDProjection> findByValikkoOrderByArvoAsc(String valikko);
}
