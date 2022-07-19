package fi.bizhop.kiekkohamsteri.projection.v1.dropdown;

import org.springframework.beans.factory.annotation.Value;

public interface PlasticDropdownProjection {
	Long getId();

	@Value("#{target.getName()}")
	String getMuovi();
}
