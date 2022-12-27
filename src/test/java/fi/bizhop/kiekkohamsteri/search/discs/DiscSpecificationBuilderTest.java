package fi.bizhop.kiekkohamsteri.search.discs;

import fi.bizhop.kiekkohamsteri.search.SearchCriteria;
import org.junit.jupiter.api.Test;

import static fi.bizhop.kiekkohamsteri.search.SearchOperation.GREATER_THAN;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DiscSpecificationBuilderTest {
    @Test
    public void givenEmptyCriteria_whenBuild_returnNull() {
        var builder = DiscSpecificationBuilder.builder();

        assertNull(builder.build());
    }

    @Test
    public void givenAtLeastOneCriteria_whenBuild_returnSpec() {
        var builder = DiscSpecificationBuilder.builder();

        var criteria = new SearchCriteria("weight", 170, GREATER_THAN);

        var spec = builder.with(criteria).build();

        assertNotNull(spec);
    }
}
