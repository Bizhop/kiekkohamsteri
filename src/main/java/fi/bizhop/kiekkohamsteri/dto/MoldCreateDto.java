package fi.bizhop.kiekkohamsteri.dto;

public class MoldCreateDto {
	private Long valmId;
	private String kiekko;
	private Double nopeus;
	private Double liito;
	private Double vakaus;
	private Double feidi;
	
	public Long getValmId() {
		return valmId;
	}
	public MoldCreateDto setValmId(Long valmId) {
		this.valmId = valmId;
		return this;
	}
	public String getKiekko() {
		return kiekko;
	}
	public MoldCreateDto setKiekko(String kiekko) {
		this.kiekko = kiekko;
		return this;
	}
	public Double getNopeus() {
		return nopeus;
	}
	public MoldCreateDto setNopeus(Double nopeus) {
		this.nopeus = nopeus;
		return this;
	}
	public Double getLiito() {
		return liito;
	}
	public MoldCreateDto setLiito(Double liito) {
		this.liito = liito;
		return this;
	}
	public Double getVakaus() {
		return vakaus;
	}
	public MoldCreateDto setVakaus(Double vakaus) {
		this.vakaus = vakaus;
		return this;
	}
	public Double getFeidi() {
		return feidi;
	}
	public MoldCreateDto setFeidi(Double feidi) {
		this.feidi = feidi;
		return this;
	}
	
}
