package fi.bizhop.kiekkohamsteri.dto;

import fi.bizhop.kiekkohamsteri.projection.v1.DiscProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ListingDto {
	String username;
	List<DiscProjection> kiekot;

	public static ListingDto fromMapEntry(Map.Entry<String, List<DiscProjection>> entry) {
		var username = entry.getValue().stream()
				.findFirst()
				.map(DiscProjection::getOmistaja);
		return new ListingDto(username.orElse(entry.getKey()), entry.getValue());
	}
}