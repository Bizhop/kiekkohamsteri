package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import fi.bizhop.kiekkohamsteri.model.Plastic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface PlasticRepository extends PagingAndSortingRepository<Plastic, Long> {
	List<Plastic> findAllByOrderByNameAsc();
	List<Plastic> findByManufacturerOrderByNameAsc(Manufacturer manufacturer);
	
	Page<Plastic> findByManufacturer(Manufacturer manufacturer, Pageable pageable);
	Integer countByCreatedAtBetween(Date beginDate, Date endDate);
}
