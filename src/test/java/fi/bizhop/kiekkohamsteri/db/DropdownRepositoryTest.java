package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.DropdownValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.REPOSITORY;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;

@SpringBootTest
@ActiveProfiles("test")
public class DropdownRepositoryTest extends SpringContextTestBase {
    @Autowired DropdownRepository dropdownRepository;

    BaseAdder adder = new BaseAdder("dropdown", REPOSITORY);

    @BeforeEach
    void setupTestData() {
        dropdownRepository.deleteAll();

        var condition = new DropdownValues();
        condition.setMenu("kunto");
        condition.setName("10/10");
        condition.setValue(10L);
        dropdownRepository.save(condition);

        var markings1 = new DropdownValues();
        markings1.setMenu("tussit");
        markings1.setName("rimmissä");
        markings1.setValue(1L);
        dropdownRepository.save(markings1);

        var markings2 = new DropdownValues();
        markings2.setMenu("tussit");
        markings2.setName("pohjassa");
        markings2.setValue(2L);
        dropdownRepository.save(markings2);
    }

    @Test
    void findByMenuOrderByValueAscTest() {
        var conditions = dropdownRepository.findByMenuOrderByValueAsc("kunto");
        var markings = dropdownRepository.findByMenuOrderByValueAsc("tussit");

        assertEqualsJson(adder.create("conditions.json"), conditions);
        assertEqualsJson(adder.create("markings.json"), markings);
    }
}
