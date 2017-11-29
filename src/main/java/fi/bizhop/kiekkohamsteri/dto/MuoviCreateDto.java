package fi.bizhop.kiekkohamsteri.dto;

public class MuoviCreateDto {
	private Long valmId;
	private String muovi;
	
	public Long getValmId() {
		return valmId;
	}
	public MuoviCreateDto setValmId(Long valmId) {
		this.valmId = valmId;
		return this;
	}
	public String getMuovi() {
		return muovi;
	}
	public MuoviCreateDto setMuovi(String muovi) {
		this.muovi = muovi;
		return this;
	}
}
