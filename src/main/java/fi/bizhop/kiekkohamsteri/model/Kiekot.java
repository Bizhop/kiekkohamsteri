package fi.bizhop.kiekkohamsteri.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "kiekot")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Kiekot extends TimestampBase {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="member_id")
	private Members member;

	@ManyToOne
	@JoinColumn(name="mold_id")
	private R_mold mold;

	@ManyToOne
	@JoinColumn(name="muovi_id")
	private R_muovi muovi;

	@ManyToOne
	@JoinColumn(name="vari_id")
	private R_vari vari;
	
	private String kuva;
	private Integer paino;
	@Column(nullable = false)
	private Integer kunto;
	private Boolean hohto;
	private Boolean spessu;
	@Column(nullable = false)
	private Boolean dyed;
	@Column(nullable = false)
	private Boolean swirly;
	@Column(nullable = false)
	private Integer tussit;
	private Boolean myynnissa;
	@Column(nullable = false)
	private Integer hinta;
	private String muuta;
	private Boolean loytokiekko;
	@Column(nullable = false)
	private Boolean itb;
	
	@Column(name="public", nullable = false)
	private Boolean publicDisc;

	@Column(nullable = false)
	private Boolean lost;
	
	public Kiekot(Members user, R_mold defaultMold, R_muovi defaultMuovi, R_vari defaultVari) {
		this.member = user;
		this.mold = defaultMold;
		this.muovi = defaultMuovi;
		this.vari = defaultVari;
		this.kuva = "";
		this.paino = 175;
		this.kunto = 10;
		this.hohto = false;
		this.spessu = false;
		this.dyed = false;
		this.swirly = false;
		this.tussit = 1;
		this.myynnissa = false;
		this.hinta = 0;
		this.muuta = "";
		this.loytokiekko = false;
		this.itb = false;
		this.publicDisc = false;
		this.lost = false;
	}
}
