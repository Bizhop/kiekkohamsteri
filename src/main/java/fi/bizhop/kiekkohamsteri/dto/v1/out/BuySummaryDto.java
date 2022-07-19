package fi.bizhop.kiekkohamsteri.dto.v1.out;

import java.util.List;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class BuySummaryDto {
	List<BuyOutputDto> myyjana;
	List<BuyOutputDto> ostajana;
}
