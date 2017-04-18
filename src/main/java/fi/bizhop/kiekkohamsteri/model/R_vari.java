package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class R_vari {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer vari_id;

	private String vari;

	public Integer getVari_id() {
		return this.vari_id;
	}

	public void setVari_id(Integer vari_id) {
		this.vari_id = vari_id;
	}

	public String getVari() {
		return this.vari;
	}

	public void setVari(String vari) {
		this.vari = vari;
	}
}
