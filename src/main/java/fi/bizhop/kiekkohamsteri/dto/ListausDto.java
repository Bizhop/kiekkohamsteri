package fi.bizhop.kiekkohamsteri.dto;

import java.util.List;

import fi.bizhop.kiekkohamsteri.projection.KiekkoProjection;

public class ListausDto {
	String username;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
