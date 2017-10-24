package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class R_vari {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String vari;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVari() {
		return this.vari;
	}

	public void setVari(String vari) {
		this.vari = vari;
	}
}
