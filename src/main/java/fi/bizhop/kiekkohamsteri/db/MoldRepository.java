package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import fi.bizhop.kiekkohamsteri.model.Mold;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface MoldRepository extends PagingAndSortingRepository<Mold, Long> {
	List<Mold> findAllByOrderByNameAsc();
	List<Mold> findByManufacturerOrderByNameAsc(Manufacturer manufacturer);
	
	Page<Mold> findByManufacturer(Manufacturer manufacturer, Pageable pageable);
	Integer countByCreatedAtBetween(Date beginDate, Date endDate);
}