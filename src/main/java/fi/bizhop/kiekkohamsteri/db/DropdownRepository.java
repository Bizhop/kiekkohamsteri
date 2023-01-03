package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.model.DropdownValues;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DropdownRepository extends CrudRepository<DropdownValues, Long> {
	List<DropdownValues> findByMenuOrderByValueAsc(String menu);
}
