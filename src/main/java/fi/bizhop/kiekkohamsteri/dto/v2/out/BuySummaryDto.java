package fi.bizhop.kiekkohamsteri.dto.v2.out;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.util.List;

@Value
@Builder
@Jacksonized
public class BuySummaryDto {
	@NotNull
	List<BuyOutputDto> asBuyer;
	@NotNull
	List<BuyOutputDto> asSeller;
}
