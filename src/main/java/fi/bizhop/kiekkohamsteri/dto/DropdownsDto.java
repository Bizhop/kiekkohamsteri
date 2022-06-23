package fi.bizhop.kiekkohamsteri.dto;

import java.util.List;

import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.DropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.MoldDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.PlasticDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.ManufacturerDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.ColorDropdownProjection;

public class DropdownsDto {
	private List<MoldDropdownProjection> molds;
	private List<ManufacturerDropdownProjection> valms;
	private List<PlasticDropdownProjection> muovit;
	private List<ColorDropdownProjection> varit;
	private List<DropdownProjection> kunto;
	private List<DropdownProjection> tussit;
	
	public List<MoldDropdownProjection> getMolds() {
		return molds;
	}
	public DropdownsDto setMolds(List<MoldDropdownProjection> molds) {
		this.molds = molds;
		return this;
	}
	public List<ManufacturerDropdownProjection> getValms() {
		return valms;
	}
	public DropdownsDto setValms(List<ManufacturerDropdownProjection> valms) {
		this.valms = valms;
		return this;
	}
	public List<PlasticDropdownProjection> getMuovit() {
		return muovit;
	}
	public DropdownsDto setMuovit(List<PlasticDropdownProjection> muovit) {
		this.muovit = muovit;
		return this;
	}
	public List<ColorDropdownProjection> getVarit() {
		return varit;
	}
	public DropdownsDto setVarit(List<ColorDropdownProjection> varit) {
		this.varit = varit;
		return this;
	}
	public List<DropdownProjection> getKunto() {
		return kunto;
	}
	public DropdownsDto setKunto(List<DropdownProjection> kunto) {
		this.kunto = kunto;
		return this;
	}
	public List<DropdownProjection> getTussit() {
		return tussit;
	}
	public DropdownsDto setTussit(List<DropdownProjection> tussit) {
		this.tussit = tussit;
		return this;
	}
}
