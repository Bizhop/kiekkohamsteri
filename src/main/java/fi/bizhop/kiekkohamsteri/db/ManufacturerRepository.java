package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.ManufacturerDropdownProjection;

import javax.annotation.Nonnull;

public interface ManufacturerRepository extends CrudRepository<Manufacturer, Long> {
	List<ManufacturerDropdownProjection> findAllProjectedBy();

	@Override
	@Nonnull
	List<Manufacturer> findAll();

	Integer countByCreatedAtBetween(Date beginDate, Date endDate);
}
