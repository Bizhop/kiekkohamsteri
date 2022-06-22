package fi.bizhop.kiekkohamsteri.projection.v1.dropdown;

import org.springframework.beans.factory.annotation.Value;

public interface DropdownProjection {
	@Value("#{target.getArvo()}")
	Integer getId();
	String getNimi();
}
