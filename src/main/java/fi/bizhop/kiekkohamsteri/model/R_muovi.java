package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "r_muovi")
public class R_muovi {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="valmistaja_id")
	private R_valm valmistaja;
	
	private String muovi;

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

	public String getMuovi() {
		return muovi;
	}

	public void setMuovi(String muovi) {
		this.muovi = muovi;
	}
}