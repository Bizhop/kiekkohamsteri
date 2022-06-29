package fi.bizhop.kiekkohamsteri.dto;

import java.util.List;

import fi.bizhop.kiekkohamsteri.model.Ostot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BuysDto {
	List<Ostot> myyjana;
	List<Ostot> ostajana;
}
