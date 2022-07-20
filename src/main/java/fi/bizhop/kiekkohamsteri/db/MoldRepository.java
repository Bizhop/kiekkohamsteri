package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fi.bizhop.kiekkohamsteri.model.Mold;
import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.MoldDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.v1.MoldProjection;

public interface MoldRepository extends PagingAndSortingRepository<Mold, Long> {
	List<MoldDropdownProjection> findAllByOrderByNameAsc();
	List<MoldDropdownProjection> findByManufacturerOrderByNameAsc(Manufacturer valmistaja);
	
	Page<MoldProjection> findAllProjectedBy(Pageable pageable);
	Page<MoldProjection> findByManufacturer(Manufacturer manufacturer, Pageable pageable);
	Integer countByCreatedAtBetween(Date beginDate, Date endDate);

	MoldProjection getMoldById(Long id);
}