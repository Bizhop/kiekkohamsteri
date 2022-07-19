package fi.bizhop.kiekkohamsteri.projection.v1.dropdown;

import org.springframework.beans.factory.annotation.Value;

public interface MoldDropdownProjection {
	Long getId();

	@Value("#{target.getName()}")
	String getKiekko();
}
