package fi.bizhop.kiekkohamsteri.projection;

import org.springframework.beans.factory.annotation.Value;

public interface MuoviProjection {
	Long getId();
	@Value("#{target.getValmistaja().getValmistaja()}")
	String getValmistaja();
	String getMuovi();
}
