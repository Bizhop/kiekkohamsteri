package fi.bizhop.kiekkohamsteri.projection.v1.dropdown;

import org.springframework.beans.factory.annotation.Value;

public interface DropdownProjection {
	@Value("#{target.getValue()}")
	Integer getId();

	@Value("#{target.getName()}")
	String getNimi();
}
