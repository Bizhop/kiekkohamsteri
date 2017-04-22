package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class R_mold {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="valmistaja_id")
	private R_valm valmistaja;
		
	private String kiekko;
	private Integer nopeus;
	private Integer liito;
	private Integer vakaus;
	private Integer feidi;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public R_valm getValmistaja() {
		return valmistaja;
	}
	public void setValmistaja(R_valm valmistaja) {
		this.valmistaja = valmistaja;
	}
	public String getKiekko() {
		return kiekko;
	}
	public void setKiekko(String kiekko) {
		this.kiekko = kiekko;
	}
	public Integer getNopeus() {
		return nopeus;
	}
	public void setNopeus(Integer nopeus) {
		this.nopeus = nopeus;
	}
	public Integer getLiito() {
		return liito;
	}
	public void setLiito(Integer liito) {
		this.liito = liito;
	}
	public Integer getVakaus() {
		return vakaus;
	}
	public void setVakaus(Integer vakaus) {
		this.vakaus = vakaus;
	}
	public Integer getFeidi() {
		return feidi;
	}
	public void setFeidi(Integer feidi) {
		this.feidi = feidi;
	}
}
