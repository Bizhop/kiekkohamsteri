package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.model.Color;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.Nonnull;
import java.util.List;

public interface ColorRepository extends CrudRepository<Color, Long> {
	@Override
	@Nonnull
	List<Color> findAll();
}
