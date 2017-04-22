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
	private Integer id;
	
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
	private Boolean hohto;
	private Boolean spessu;
	private Boolean dyed;
	private Boolean swirly;
	private Boolean tussit;
	private Boolean myynnissa;
	private Integer hinta;
	private String muuta;
	private Boolean loytokiekko;
	private Boolean itb;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
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
	public Boolean getHohto() {
		return hohto;
	}
	public void setHohto(Boolean hohto) {
		this.hohto = hohto;
	}
	public Boolean getSpessu() {
		return spessu;
	}
	public void setSpessu(Boolean spessu) {
		this.spessu = spessu;
	}
	public Boolean getDyed() {
		return dyed;
	}
	public void setDyed(Boolean dyed) {
		this.dyed = dyed;
	}
	public Boolean getSwirly() {
		return swirly;
	}
	public void setSwirly(Boolean swirly) {
		this.swirly = swirly;
	}
	public Boolean getTussit() {
		return tussit;
	}
	public void setTussit(Boolean tussit) {
		this.tussit = tussit;
	}
	public Boolean getMyynnissa() {
		return myynnissa;
	}
	public void setMyynnissa(Boolean myynnissa) {
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
	public Boolean getLoytokiekko() {
		return loytokiekko;
	}
	public void setLoytokiekko(Boolean loytokiekko) {
		this.loytokiekko = loytokiekko;
	}
	public Boolean getItb() {
		return itb;
	}
	public void setItb(Boolean itb) {
		this.itb = itb;
	}
}
