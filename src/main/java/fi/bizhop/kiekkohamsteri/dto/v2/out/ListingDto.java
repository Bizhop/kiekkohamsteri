package fi.bizhop.kiekkohamsteri.dto.v2.out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListingDto {
	String username;
	List<DiscOutputDto> discs;
}
