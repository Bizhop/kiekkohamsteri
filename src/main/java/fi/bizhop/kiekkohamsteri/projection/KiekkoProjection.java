package fi.bizhop.kiekkohamsteri.projection;

import org.springframework.beans.factory.annotation.Value;

public interface KiekkoProjection {
	Integer getId();	
	@Value("#{target.getMember().getUsername()}")
	String getOmistaja();
	@Value("#{target.getMold().getValmistaja().getValmistaja()}")
	String getValmistaja();
	@Value("#{target.getMold().getKiekko()}")
	String getMold();
	@Value("#{target.getMuovi().getMuovi()}")
	String getMuovi();
	@Value("#{target.getVari().getVari()}")
	String getVari();
	String getKuva();
	Integer getPaino();
	Integer getKunto();
	Integer getHohto();
	Integer getSpessu();
	Integer getDyed();
	Integer getSwirly();
	Integer getTussit();
	Integer getMyynnissa();
	Integer getHinta();
	String getMuuta();
	Integer getLoytokiekko();
	Integer getItb();
}
