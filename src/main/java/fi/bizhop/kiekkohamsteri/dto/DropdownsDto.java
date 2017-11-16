package fi.bizhop.kiekkohamsteri.dto;

import java.util.List;

import fi.bizhop.kiekkohamsteri.projection.DDProjection;
import fi.bizhop.kiekkohamsteri.projection.MoldDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.MuoviDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.ValmDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.VariDropdownProjection;

public class DropdownsDto {
	private List<MoldDropdownProjection> molds;
	private List<ValmDropdownProjection> valms;
	private List<MuoviDropdownProjection> muovit;
	private List<VariDropdownProjection> varit;
	private List<DDProjection> kunto;
	private List<DDProjection> tussit;
	
	public List<MoldDropdownProjection> getMolds() {
		return molds;
	}
	public DropdownsDto setMolds(List<MoldDropdownProjection> molds) {
		this.molds = molds;
		return this;
	}
	public List<ValmDropdownProjection> getValms() {
		return valms;
	}
	public DropdownsDto setValms(List<ValmDropdownProjection> valms) {
		this.valms = valms;
		return this;
	}
	public List<MuoviDropdownProjection> getMuovit() {
		return muovit;
	}
	public DropdownsDto setMuovit(List<MuoviDropdownProjection> muovit) {
		this.muovit = muovit;
		return this;
	}
	public List<VariDropdownProjection> getVarit() {
		return varit;
	}
	public DropdownsDto setVarit(List<VariDropdownProjection> varit) {
		this.varit = varit;
		return this;
	}
	public List<DDProjection> getKunto() {
		return kunto;
	}
	public DropdownsDto setKunto(List<DDProjection> kunto) {
		this.kunto = kunto;
		return this;
	}
	public List<DDProjection> getTussit() {
		return tussit;
	}
	public DropdownsDto setTussit(List<DDProjection> tussit) {
		this.tussit = tussit;
		return this;
	}
}
