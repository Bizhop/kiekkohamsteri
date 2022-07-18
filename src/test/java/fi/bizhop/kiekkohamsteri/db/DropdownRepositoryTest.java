package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.DDArvot;
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

        var condition = new DDArvot();
        condition.setValikko("kunto");
        condition.setNimi("10/10");
        condition.setArvo(10);
        dropdownRepository.save(condition);

        var markings1 = new DDArvot();
        markings1.setValikko("tussit");
        markings1.setNimi("rimmissä");
        markings1.setArvo(1);
        dropdownRepository.save(markings1);

        var markings2 = new DDArvot();
        markings2.setValikko("tussit");
        markings2.setNimi("pohjassa");
        markings2.setArvo(2);
        dropdownRepository.save(markings2);
    }

    @Test
    void findByValikkoOrderByArvoAscTest() {
        var conditions = dropdownRepository.findByValikkoOrderByArvoAsc("kunto");
        var markings = dropdownRepository.findByValikkoOrderByArvoAsc("tussit");

        assertEqualsJson(adder.create("conditions.json"), conditions);
        assertEqualsJson(adder.create("markings.json"), markings);
    }
}
