package fi.bizhop.kiekkohamsteri.dto;

import fi.bizhop.kiekkohamsteri.projection.v1.DiscProjection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListausDto {
	String username;
	List<DiscProjection> kiekot;
}
