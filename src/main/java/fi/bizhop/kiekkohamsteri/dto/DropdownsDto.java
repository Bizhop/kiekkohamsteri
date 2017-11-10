package fi.bizhop.kiekkohamsteri.dto;

import java.util.List;

import fi.bizhop.kiekkohamsteri.projection.MoldDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.ValmDropdownProjection;

public class DropdownsDto {
	private List<MoldDropdownProjection> molds;
	private List<ValmDropdownProjection> valms;
	
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
}
