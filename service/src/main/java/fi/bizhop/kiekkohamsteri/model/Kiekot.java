package fi.bizhop.kiekkohamsteri.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Kiekot {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
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
	private Integer kunto;
	private Integer hohto;
	private Integer spessu;
	private Integer dyed;
	private Integer swirly;
	private Integer tussit;
	private Integer myynnissa;
	private Integer hinta;
	private String muuta;
	private Integer loytokiekko;
	private Integer itb;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Members getMember() {
		return member;
	}
	public void setMember(Members member) {
		this.member = member;
	}
	public R_mold getMold() {
		return mold;
	}
	public void setMold(R_mold mold) {
		this.mold = mold;
	}
	public R_muovi getMuovi() {
		return muovi;
	}
	public void setMuovi(R_muovi muovi) {
		this.muovi = muovi;
	}
	public R_vari getVari() {
		return vari;
	}
	public void setVari(R_vari vari) {
		this.vari = vari;
	}
	public String getKuva() {
		return kuva;
	}
	public void setKuva(String kuva) {
		this.kuva = kuva;
	}
	public Integer getPaino() {
		return paino;
	}
	public void setPaino(Integer paino) {
		this.paino = paino;
	}
	public Integer getKunto() {
		return kunto;
	}
	public void setKunto(Integer kunto) {
		this.kunto = kunto;
	}
	public Integer getHohto() {
		return hohto;
	}
	public void setHohto(Integer hohto) {
		this.hohto = hohto;
	}
	public Integer getSpessu() {
		return spessu;
	}
	public void setSpessu(Integer spessu) {
		this.spessu = spessu;
	}
	public Integer getDyed() {
		return dyed;
	}
	public void setDyed(Integer dyed) {
		this.dyed = dyed;
	}
	public Integer getSwirly() {
		return swirly;
	}
	public void setSwirly(Integer swirly) {
		this.swirly = swirly;
	}
	public Integer getTussit() {
		return tussit;
	}
	public void setTussit(Integer tussit) {
		this.tussit = tussit;
	}
	public Integer getMyynnissa() {
		return myynnissa;
	}
	public void setMyynnissa(Integer myynnissa) {
		this.myynnissa = myynnissa;
	}
	public Integer getHinta() {
		return hinta;
	}
	public void setHinta(Integer hinta) {
		this.hinta = hinta;
	}
	public String getMuuta() {
		return muuta;
	}
	public void setMuuta(String muuta) {
		this.muuta = muuta;
	}
	public Integer getLoytokiekko() {
		return loytokiekko;
	}
	public void setLoytokiekko(Integer loytokiekko) {
		this.loytokiekko = loytokiekko;
	}
	public Integer getItb() {
		return itb;
	}
	public void setItb(Integer itb) {
		this.itb = itb;
	}
}
