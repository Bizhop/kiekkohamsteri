package fi.bizhop.kiekkohamsteri.dto;

public class UserUpdateDto {
	private String username;
	private String etunimi;
	private String sukunimi;
	private Integer pdga_num;
	private Boolean publicDiscCount;
	private Boolean publicList;
	
	public String getUsername() {
		return username;
	}
	public UserUpdateDto setUsername(String username) {
		this.username = username;
		return this;
	}
	public String getEtunimi() {
		return etunimi;
	}
	public UserUpdateDto setEtunimi(String etunimi) {
		this.etunimi = etunimi;
		return this;
	}
	public String getSukunimi() {
		return sukunimi;
	}
	public UserUpdateDto setSukunimi(String sukunimi) {
		this.sukunimi = sukunimi;
		return this;
	}
	public Integer getPdga_num() {
		return pdga_num;
	}
	public UserUpdateDto setPdga_num(Integer pdga_num) {
		this.pdga_num = pdga_num;
		return this;
	}
	public Boolean getPublicDiscCount() {
		return publicDiscCount;
	}
	public void setPublicDiscCount(Boolean publicDiscCount) {
		this.publicDiscCount = publicDiscCount;
	}
	public Boolean getPublicList() {
		return publicList;
	}
	public void setPublicList(Boolean publicList) {
		this.publicList = publicList;
	}
}
