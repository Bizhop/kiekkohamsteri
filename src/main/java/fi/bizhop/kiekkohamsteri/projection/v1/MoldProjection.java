package fi.bizhop.kiekkohamsteri.projection.v1;

import org.springframework.beans.factory.annotation.Value;

public interface MoldProjection {
	Long getId();
	@Value("#{target.getValmistaja().getValmistaja()}")
	String getValmistaja();
	String getKiekko();
	Double getNopeus();
	Double getLiito();
	Double getVakaus();
	Double getFeidi();
}
