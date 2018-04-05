package fi.bizhop.kiekkohamsteri.dto;

import java.util.List;

import fi.bizhop.kiekkohamsteri.model.Ostot;

public class OstotDto {
	private List<Ostot> myyjana;
	private List<Ostot> ostajana;
	
	public OstotDto() {
		super();
	}
	
	public OstotDto(List<Ostot> myyjana, List<Ostot> ostajana) {
		super();
		this.myyjana = myyjana;
		this.ostajana = ostajana;
	}

	public List<Ostot> getMyyjana() {
		return myyjana;
	}

	public void setMyyjana(List<Ostot> myyjana) {
		this.myyjana = myyjana;
	}

	public List<Ostot> getOstajana() {
		return ostajana;
	}

	public void setOstajana(List<Ostot> ostajana) {
		this.ostajana = ostajana;
	}
}
