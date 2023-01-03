package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;

public interface ManufacturerRepository extends CrudRepository<Manufacturer, Long> {
	@Override
	@Nonnull
	List<Manufacturer> findAll();

	Integer countByCreatedAtBetween(Date beginDate, Date endDate);
}
