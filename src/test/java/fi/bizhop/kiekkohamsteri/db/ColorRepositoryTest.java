package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.REPOSITORY;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;

@SpringBootTest
@ActiveProfiles("test")
public class ColorRepositoryTest extends SpringContextTestBase {
    @Autowired ColorRepository colorRepository;

    BaseAdder adder = new BaseAdder("color", REPOSITORY);

    @BeforeEach
    void setupTestData() {
        colorRepository.deleteAll();
        var testColor = new Color(0L, "test color");
        colorRepository.save(testColor);
    }

    @Test
    void findAllProjectedByTest() {
        var result = colorRepository.findAllProjectedBy();

        assertEqualsJson(adder.create("dropdownProjections.json"), result);
    }
}
