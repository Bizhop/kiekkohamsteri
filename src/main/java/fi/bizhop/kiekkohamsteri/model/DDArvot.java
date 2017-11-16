package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dd_arvot")
public class DDArvot {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String valikko;
	private String nimi;
	private Integer arvo;
	
	public Long getId() {
		return id;
	}
	public DDArvot setId(Long id) {
		this.id = id;
		return this;
	}
	public String getValikko() {
		return valikko;
	}
	public DDArvot setValikko(String valikko) {
		this.valikko = valikko;
		return this;
	}
	public String getNimi() {
		return nimi;
	}
	public DDArvot setNimi(String nimi) {
		this.nimi = nimi;
		return this;
	}
	public Integer getArvo() {
		return arvo;
	}
	public DDArvot setArvo(Integer arvo) {
		this.arvo = arvo;
		return this;
	}
}
