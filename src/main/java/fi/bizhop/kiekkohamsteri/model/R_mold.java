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
	public R_mold setId(Long id) {
		this.id = id;
		return this;
	}
	public R_valm getValmistaja() {
		return valmistaja;
	}
	public R_mold setValmistaja(R_valm valmistaja) {
		this.valmistaja = valmistaja;
		return this;
	}
	public String getKiekko() {
		return kiekko;
	}
	public R_mold setKiekko(String kiekko) {
		this.kiekko = kiekko;
		return this;
	}
	public Double getNopeus() {
		return nopeus;
	}
	public R_mold setNopeus(Double nopeus) {
		this.nopeus = nopeus;
		return this;
	}
	public Double getLiito() {
		return liito;
	}
	public R_mold setLiito(Double liito) {
		this.liito = liito;
		return this;
	}
	public Double getVakaus() {
		return vakaus;
	}
	public R_mold setVakaus(Double vakaus) {
		this.vakaus = vakaus;
		return this;
	}
	public Double getFeidi() {
		return feidi;
	}
	public R_mold setFeidi(Double feidi) {
		this.feidi = feidi;
		return this;
	}
}
