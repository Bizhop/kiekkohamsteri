package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "members")
public class Members {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String username;
	private String email;
	private Integer level;
	private String etunimi;
	private String sukunimi;
	private Integer pdga_num;
	
	@Column(name="public_list")
	@NotNull
	private Boolean publicList;
	
	@Column(name="public_disc_count")
	@NotNull
	private Boolean publicDiscCount;
	
	public Members() {}
	
	public Members(String userEmail) {
		this.username = "Uusi käyttäjä";
		this.email = userEmail;
		this.level = 1;
		this.etunimi = "Uusi";
		this.sukunimi = "Käyttäjä";
		this.pdga_num = 0;
		this.publicList = false;
		this.publicDiscCount = false;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getEtunimi() {
		return etunimi;
	}
	public void setEtunimi(String etunimi) {
		this.etunimi = etunimi;
	}
	public String getSukunimi() {
		return sukunimi;
	}
	public void setSukunimi(String sukunimi) {
		this.sukunimi = sukunimi;
	}
	public Integer getPdga_num() {
		return pdga_num;
	}
	public void setPdga_num(Integer pdga_num) {
		this.pdga_num = pdga_num;
	}
	public Boolean getPublicList() {
		return publicList;
	}
	public void setPublicList(Boolean publicList) {
		this.publicList = publicList;
	}
	public Boolean getPublicDiscCount() {
		return publicDiscCount;
	}
	public void setPublicDiscCount(Boolean publicDiscCount) {
		this.publicDiscCount = publicDiscCount;
	}
}