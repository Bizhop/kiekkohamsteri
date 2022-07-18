package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.REPOSITORY;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static fi.bizhop.kiekkohamsteri.TestUtils.dateWithDayOffset;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class ManufacturerRepositoryTest extends SpringContextTestBase {
    @Autowired ManufacturerRepository manufacturerRepository;

    BaseAdder adder = new BaseAdder("manufacturer", REPOSITORY);

    @BeforeEach
    void setupTestData() {
        manufacturerRepository.deleteAll();

        var innova = new R_valm();
        innova.setValmistaja("Innova");
        manufacturerRepository.save(innova);

        var discmania = new R_valm();
        discmania.setValmistaja("Discmania");
        manufacturerRepository.save(discmania);
    }

    @Test
    void findAllProjectedByTest() {
        var all = manufacturerRepository.findAllProjectedBy();

        assertEqualsJson(adder.create("all.json"), all);
    }

    @Test
    void countByCreatedAtBetweenTest() {
        var today = new Date();
        var yesterday = dateWithDayOffset(today, -1);
        var tomorrow = dateWithDayOffset(today, 1);
        var dayAfterTomorrow = dateWithDayOffset(today, 2);

        var insideRange = manufacturerRepository.countByCreatedAtBetween(yesterday, tomorrow);
        var outsideRange = manufacturerRepository.countByCreatedAtBetween(tomorrow, dayAfterTomorrow);

        assertEquals(2, insideRange);
        assertEquals(0, outsideRange);
    }
}
