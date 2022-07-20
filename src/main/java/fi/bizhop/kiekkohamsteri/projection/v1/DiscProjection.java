package fi.bizhop.kiekkohamsteri.projection.v1;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface DiscProjection {
	Long getId();

	@Value("#{target.getOwner().getUsername()}")
	String getOmistaja();

	@Value("#{target.getOwner().getEmail()}")
	String getOwnerEmail();

	@Value("#{target.getMold().getManufacturer().getName()}")
	String getValmistaja();

	@Value("#{target.getMold().getManufacturer().getId()}")
	Long getValmId();

	@Value("#{target.getMold().getName()}")
	String getMold();

	@Value("#{target.getMold().getId()}")
	Long getMoldId();

	@Value("#{target.getPlastic().getName()}")
	String getMuovi();

	@Value("#{target.getPlastic().getId()}")
	Long getMuoviId();

	@Value("#{target.getColor().getName()}")
	String getVari();

	@Value("#{target.getColor().getId()}")
	Long getVariId();

	@Value("#{target.getMold().getSpeed()}")
	Double getNopeus();

	@Value("#{target.getMold().getGlide()}")
	Double getLiito();

	@Value("#{target.getMold().getStability()}")
	Double getVakaus();

	@Value("#{target.getMold().getFade()}")
	Double getFeidi();

	@Value("#{target.getImage()}")
	String getKuva();

	@Value("#{target.getWeight()}")
	Integer getPaino();

	@Value("#{target.getCondition()}")
	Integer getKunto();

	@Value("#{target.getGlow()}")
	Boolean getHohto();

	@Value("#{target.getSpecial()}")
	Boolean getSpessu();

	Boolean getDyed();
	Boolean getSwirly();

	@Value("#{target.getMarkings()}")
	Integer getTussit();

	@Value("#{target.getForSale()}")
	Boolean getMyynnissa();

	@Value("#{target.getPrice()}")
	Integer getHinta();

	@Value("#{target.getDescription()}")
	String getMuuta();

	@Value("#{target.getLostAndFound()}")
	Boolean getLoytokiekko();

	Boolean getItb();
	Boolean getPublicDisc();
	Boolean getLost();

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "fi_FI")
	Date getCreatedAt();
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "fi_FI")
	Date getUpdatedAt();
}
