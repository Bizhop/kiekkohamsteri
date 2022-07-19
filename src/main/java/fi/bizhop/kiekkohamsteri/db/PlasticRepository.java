package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fi.bizhop.kiekkohamsteri.model.Plastic;
import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.PlasticDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.v1.PlasticProjection;

public interface PlasticRepository extends PagingAndSortingRepository<Plastic, Long> {
	List<PlasticDropdownProjection> findAllByOrderByNameAsc();
	List<PlasticDropdownProjection> findByManufacturerOrderByNameAsc(Manufacturer manufacturer);
	
	Page<PlasticProjection> findAllProjectedBy(Pageable pageable);
	Page<PlasticProjection> findByManufacturer(Manufacturer manufacturer, Pageable pageable);
	Integer countByCreatedAtBetween(Date beginDate, Date endDate);

	PlasticProjection getPlasticById(Long id);
}
