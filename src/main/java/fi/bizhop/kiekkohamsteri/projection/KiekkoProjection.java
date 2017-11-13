package fi.bizhop.kiekkohamsteri.projection;

import org.springframework.beans.factory.annotation.Value;

public interface KiekkoProjection {
	Integer getId();	
	@Value("#{target.getMember().getUsername()}")
	String getOmistaja();
	@Value("#{target.getMold().getValmistaja().getValmistaja()}")
	String getValmistaja();
	@Value("#{target.getMold().getValmistaja().getId()}")
	Long getValmId();
	@Value("#{target.getMold().getKiekko()}")
	String getMold();
	@Value("#{target.getMold().getId()}")
	Long getMoldId();
	@Value("#{target.getMuovi().getMuovi()}")
	String getMuovi();
	@Value("#{target.getMuovi().getId()}")
	Long getMuoviId();
	@Value("#{target.getVari().getVari()}")
	String getVari();
	@Value("#{target.getVari().getId()}")
	Long getVariId();
	@Value("#{target.getMold().getNopeus()}")
	Double getNopeus();
	@Value("#{target.getMold().getLiito()}")
	Double getLiito();
	@Value("#{target.getMold().getVakaus()}")
	Double getVakaus();
	@Value("#{target.getMold().getFeidi()}")
	Double getFeidi();
	String getKuva();
	Integer getPaino();
	Integer getKunto();
	Boolean getHohto();
	Boolean getSpessu();
	Boolean getDyed();
	Boolean getSwirly();
	Integer getTussit();
	Boolean getMyynnissa();
	Integer getHinta();
	String getMuuta();
	Boolean getLoytokiekko();
	Boolean getItb();
}
