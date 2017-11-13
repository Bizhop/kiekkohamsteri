package fi.bizhop.kiekkohamsteri.dto;

public class KiekkoDto {
	private Long moldId;
	private Long muoviId;
	private Long variId;
	private String kuva;
	private Integer paino;
	private Integer kunto;
	private Boolean hohto;
	private Boolean spessu;
	private Boolean dyed;
	private Boolean swirly;
	private Integer tussit;
	private Boolean myynnissa;
	private Integer hinta;
	private String muuta;
	private Boolean loytokiekko;
	private Boolean itb;
	
	public Long getMoldId() {
		return moldId;
	}
	public KiekkoDto setMoldId(Long moldId) {
		this.moldId = moldId;
		return this;
	}
	public Long getMuoviId() {
		return muoviId;
	}
	public KiekkoDto setMuoviId(Long muoviId) {
		this.muoviId = muoviId;
		return this;
	}
	public Long getVariId() {
		return variId;
	}
	public KiekkoDto setVariId(Long variId) {
		this.variId = variId;
		return this;
	}
	public String getKuva() {
		return kuva;
	}
	public KiekkoDto setKuva(String kuva) {
		this.kuva = kuva;
		return this;
	}
	public Integer getPaino() {
		return paino;
	}
	public KiekkoDto setPaino(Integer paino) {
		this.paino = paino;
		return this;
	}
	public Integer getKunto() {
		return kunto;
	}
	public KiekkoDto setKunto(Integer kunto) {
		this.kunto = kunto;
		return this;
	}
	public Boolean getHohto() {
		return hohto;
	}
	public KiekkoDto setHohto(Boolean hohto) {
		this.hohto = hohto;
		return this;
	}
	public Boolean getSpessu() {
		return spessu;
	}
	public KiekkoDto setSpessu(Boolean spessu) {
		this.spessu = spessu;
		return this;
	}
	public Boolean getDyed() {
		return dyed;
	}
	public KiekkoDto setDyed(Boolean dyed) {
		this.dyed = dyed;
		return this;
	}
	public Boolean getSwirly() {
		return swirly;
	}
	public KiekkoDto setSwirly(Boolean swirly) {
		this.swirly = swirly;
		return this;
	}
	public Integer getTussit() {
		return tussit;
	}
	public KiekkoDto setTussit(Integer tussit) {
		this.tussit = tussit;
		return this;
	}
	public Boolean getMyynnissa() {
		return myynnissa;
	}
	public KiekkoDto setMyynnissa(Boolean myynnissa) {
		this.myynnissa = myynnissa;
		return this;
	}
	public Integer getHinta() {
		return hinta;
	}
	public KiekkoDto setHinta(Integer hinta) {
		this.hinta = hinta;
		return this;
	}
	public String getMuuta() {
		return muuta;
	}
	public KiekkoDto setMuuta(String muuta) {
		this.muuta = muuta;
		return this;
	}
	public Boolean getLoytokiekko() {
		return loytokiekko;
	}
	public KiekkoDto setLoytokiekko(Boolean loytokiekko) {
		this.loytokiekko = loytokiekko;
		return this;
	}
	public Boolean getItb() {
		return itb;
	}
	public KiekkoDto setItb(Boolean itb) {
		this.itb = itb;
		return this;
	}
	
}
