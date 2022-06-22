package fi.bizhop.kiekkohamsteri.projection.v1;

import org.springframework.beans.factory.annotation.Value;

public interface PlasticProjection {
	Long getId();
	@Value("#{target.getValmistaja().getValmistaja()}")
	String getValmistaja();
	String getMuovi();
}
