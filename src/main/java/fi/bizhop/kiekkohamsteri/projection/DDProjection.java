package fi.bizhop.kiekkohamsteri.projection;

import org.springframework.beans.factory.annotation.Value;

public interface DDProjection {
	@Value("#{target.getArvo()}")
	Integer getId();
	String getNimi();
}
