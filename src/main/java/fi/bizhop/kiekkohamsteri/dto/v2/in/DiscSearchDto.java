package fi.bizhop.kiekkohamsteri.dto.v2.in;

import fi.bizhop.kiekkohamsteri.search.SearchCriteria;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class DiscSearchDto {
    List<SearchCriteria> criteria;
}
