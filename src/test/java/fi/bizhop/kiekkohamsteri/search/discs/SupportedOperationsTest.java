package fi.bizhop.kiekkohamsteri.search.discs;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.util.Utils;
import org.junit.jupiter.api.Test;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.OTHER;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;

public class SupportedOperationsTest {
    BaseAdder adder = new BaseAdder("util", OTHER);

    @Test
    void getSupportedOperationsTest() {
        var supportedOperations = Utils.getSupportedOperations();

        assertEqualsJson(adder.create("supportedOperations.json"), supportedOperations);
    }
}
