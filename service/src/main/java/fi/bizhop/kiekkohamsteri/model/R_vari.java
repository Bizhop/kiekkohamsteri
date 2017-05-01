package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class R_vari {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	private String vari;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getVari() {
		return this.vari;
	}

	public void setVari(String vari) {
		this.vari = vari;
	}
}
