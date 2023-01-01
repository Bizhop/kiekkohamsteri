package fi.bizhop.kiekkohamsteri.dto.v2.out;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class BuySummaryDto {
	List<BuyOutputDto> asBuyer;
	List<BuyOutputDto> asSeller;
}
