package fi.bizhop.kiekkohamsteri.search.discs;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.util.Utils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.OTHER;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static fi.bizhop.kiekkohamsteri.search.SearchOperation.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupportedOperationsTest {
    BaseAdder adder = new BaseAdder("util", OTHER);

    @Test
    void getSupportedOperationsTest() {
        var supportedOperations = Utils.getSupportedOperations();

        assertEqualsJson(adder.create("supportedOperations.json"), supportedOperations);
    }

    @Test
    void whenGetSupportedOperations_thenHaveCorrectOperationOrdering() {
        var supportedOperations = Utils.getSupportedOperations();

        var weight = supportedOperations.stream()
                .filter(supportedOperation -> "weight".equals(supportedOperation.getField()))
                .findFirst()
                .orElseThrow();

        var weightOperationsList = new ArrayList<>(weight.getOperations());

        var expectedGt = weightOperationsList.get(0);
        assertEquals(GREATER_THAN, expectedGt);
        var expectedGte = weightOperationsList.get(1);
        assertEquals(GREATER_THAN_EQUAL, expectedGte);
        var expectedLt = weightOperationsList.get(2);
        assertEquals(LESS_THAN, expectedLt);
        var expectedLte = weightOperationsList.get(3);
        assertEquals(LESS_THAN_EQUAL, expectedLte);
        var expectedEq = weightOperationsList.get(4);
        assertEquals(EQUAL, expectedEq);
        var expectedNe = weightOperationsList.get(5);
        assertEquals(NOT_EQUAL, expectedNe);
        var expectedIn = weightOperationsList.get(6);
        assertEquals(IN, expectedIn);
        var expectedNin = weightOperationsList.get(7);
        assertEquals(NOT_IN, expectedNin);
    }
}
