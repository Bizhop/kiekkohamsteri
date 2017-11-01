package fi.bizhop.kiekkohamsteri.dto;

import java.util.List;

import fi.bizhop.kiekkohamsteri.projection.KiekkoProjection;

public class ListausDto {
	List<KiekkoProjection> kiekot;

	public ListausDto(List<KiekkoProjection> kiekot) {
		super();
		this.kiekot = kiekot;
	}

	public List<KiekkoProjection> getKiekot() {
		return kiekot;
	}

	public void setKiekot(List<KiekkoProjection> kiekot) {
		this.kiekot = kiekot;
	}
}
