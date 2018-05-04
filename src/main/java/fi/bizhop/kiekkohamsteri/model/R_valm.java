package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "r_valm")
public class R_valm extends TimestampBase {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String valmistaja;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValmistaja() {
		return valmistaja;
	}

	public void setValmistaja(String valmistaja) {
		this.valmistaja = valmistaja;
	}
}