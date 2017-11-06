package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "kiekot")
public class Kiekot {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="member_id")
	private Members member;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="mold_id")
	private R_mold mold;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="muovi_id")
	private R_muovi muovi;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="vari_id")
	private R_vari vari;
	
	private String kuva;
	private Integer paino;
	@NotNull
	private Integer kunto;
	private Integer hohto;
	private Integer spessu;
	@NotNull
	private Integer dyed;
	@NotNull
	private Integer swirly;
	@NotNull
	private Integer tussit;
	private Integer myynnissa;
	@NotNull
	private Integer hinta;
	private String muuta;
	private Integer loytokiekko;
	@NotNull
	private Integer itb;
	
	public Kiekot() {} //default constructor
	
	public Kiekot(Members user, R_mold defaultMold, R_muovi defaultMuovi, R_vari defaultVari) {
		this.member = user;
		this.mold = defaultMold;
		this.muovi = defaultMuovi;
		this.vari = defaultVari;
		this.kuva = "";
		this.kunto = 10;
		this.dyed = 0;
		this.swirly = 0;
		this.tussit = 0;
		this.hinta = 0;
		this.itb = 0;
	}
	
	public Long getId() {
		return id;
	}
	public Kiekot setId(Long id) {
		this.id = id;
		return this;
	}
	public Members getMember() {
		return member;
	}
	public Kiekot setMember(Members member) {
		this.member = member;
		return this;
	}
	public R_mold getMold() {
		return mold;
	}
	public Kiekot setMold(R_mold mold) {
		this.mold = mold;
		return this;
	}
	public R_muovi getMuovi() {
		return muovi;
	}
	public Kiekot setMuovi(R_muovi muovi) {
		this.muovi = muovi;
		return this;
	}
	public R_vari getVari() {
		return vari;
	}
	public Kiekot setVari(R_vari vari) {
		this.vari = vari;
		return this;
	}
	public String getKuva() {
		return kuva;
	}
	public Kiekot setKuva(String kuva) {
		this.kuva = kuva;
		return this;
	}
	public Integer getPaino() {
		return paino;
	}
	public Kiekot setPaino(Integer paino) {
		this.paino = paino;
		return this;
	}
	public Integer getKunto() {
		return kunto;
	}
	public Kiekot setKunto(Integer kunto) {
		this.kunto = kunto;
		return this;
	}
	public Integer getHohto() {
		return hohto;
	}
	public Kiekot setHohto(Integer hohto) {
		this.hohto = hohto;
		return this;
	}
	public Integer getSpessu() {
		return spessu;
	}
	public Kiekot setSpessu(Integer spessu) {
		this.spessu = spessu;
		return this;
	}
	public Integer getDyed() {
		return dyed;
	}
	public Kiekot setDyed(Integer dyed) {
		this.dyed = dyed;
		return this;
	}
	public Integer getSwirly() {
		return swirly;
	}
	public Kiekot setSwirly(Integer swirly) {
		this.swirly = swirly;
		return this;
	}
	public Integer getTussit() {
		return tussit;
	}
	public Kiekot setTussit(Integer tussit) {
		this.tussit = tussit;
		return this;
	}
	public Integer getMyynnissa() {
		return myynnissa;
	}
	public Kiekot setMyynnissa(Integer myynnissa) {
		this.myynnissa = myynnissa;
		return this;
	}
	public Integer getHinta() {
		return hinta;
	}
	public Kiekot setHinta(Integer hinta) {
		this.hinta = hinta;
		return this;
	}
	public String getMuuta() {
		return muuta;
	}
	public Kiekot setMuuta(String muuta) {
		this.muuta = muuta;
		return this;
	}
	public Integer getLoytokiekko() {
		return loytokiekko;
	}
	public Kiekot setLoytokiekko(Integer loytokiekko) {
		this.loytokiekko = loytokiekko;
		return this;
	}
	public Integer getItb() {
		return itb;
	}
	public Kiekot setItb(Integer itb) {
		this.itb = itb;
		return this;
	}
}
