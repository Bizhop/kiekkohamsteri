package fi.bizhop.kiekkohamsteri.projection.v1;

import org.springframework.beans.factory.annotation.Value;

public interface MoldProjection {
	Long getId();

	@Value("#{target.getManufacturer().getName()}")
	String getValmistaja();

	@Value("#{target.getName()}")
	String getKiekko();

	@Value("#{target.getSpeed()}")
	Double getNopeus();

	@Value("#{target.getGlide()}")
	Double getLiito();

	@Value("#{target.getStability()}")
	Double getVakaus();

	@Value("#{target.getFade()}")
	Double getFeidi();
}
