package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "r_mold")
public class R_mold {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="valmistaja_id")
	private R_valm valmistaja;
		
	private String kiekko;
	private Double nopeus;
	private Double liito;
	private Double vakaus;
	private Double feidi;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
	public Double getNopeus() {
		return nopeus;
	}
	public void setNopeus(Double nopeus) {
		this.nopeus = nopeus;
	}
	public Double getLiito() {
		return liito;
	}
	public void setLiito(Double liito) {
		this.liito = liito;
	}
	public Double getVakaus() {
		return vakaus;
	}
	public void setVakaus(Double vakaus) {
		this.vakaus = vakaus;
	}
	public Double getFeidi() {
		return feidi;
	}
	public void setFeidi(Double feidi) {
		this.feidi = feidi;
	}
}
