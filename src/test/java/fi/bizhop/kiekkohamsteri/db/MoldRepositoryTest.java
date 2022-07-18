package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.R_mold;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.REPOSITORY;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static fi.bizhop.kiekkohamsteri.TestUtils.dateWithDayOffset;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class MoldRepositoryTest extends SpringContextTestBase {
    @Autowired ManufacturerRepository manufacturerRepository;

    @Autowired MoldRepository moldRepository;

    BaseAdder adder = new BaseAdder("mold", REPOSITORY);

    @BeforeEach
    void setupTestData() {
        moldRepository.deleteAll();
        manufacturerRepository.deleteAll();

        var innova = manufacturerRepository.save(new R_valm(null, "Innova"));
        var discmania = manufacturerRepository.save(new R_valm(null, "Discmania"));

        moldRepository.save(new R_mold(null, discmania, "PD", 10.0, 4.0, 0.0, 3.0));
        moldRepository.save(new R_mold(null, discmania, "FD", 7.0, 6.0, -1.0, 1.0));
        moldRepository.save(new R_mold(null, innova, "Destroyer", 12.0, 5.0, -1.0, 3.0));
        moldRepository.save(new R_mold(null, innova, "Roc", 4.0, 4.0, 0.0, 3.0));
    }

    @Test
    void findAllByOrderByKiekkoAscTest() {
        var result = moldRepository.findAllByOrderByKiekkoAsc();

        assertEqualsJson(adder.create("allDD.json"), result);
    }

    @Test
    void findByValmistajaOrderByKiekkoAscTest() {
        var innovaResult = moldRepository.findByValmistajaOrderByKiekkoAsc(findManufacturer("Innova"));
        var discmaniaResult = moldRepository.findByValmistajaOrderByKiekkoAsc(findManufacturer("Discmania"));

        assertEqualsJson(adder.create("innovaDD.json"), innovaResult);
        assertEqualsJson(adder.create("discmaniaDD.json"), discmaniaResult);
    }

    @Test
    void findAllProjectedByTest() {
        var result = moldRepository.findAllProjectedBy(Pageable.unpaged());

        assertEqualsJson(adder.create("all.json"), result);
    }

    @Test
    void findByValmistajaTest() {
        var innovaResult = moldRepository.findByValmistaja(findManufacturer("Innova"), Pageable.unpaged());
        var discmaniaResult = moldRepository.findByValmistaja(findManufacturer("Discmania"), Pageable.unpaged());

        assertEqualsJson(adder.create("innova.json"), innovaResult);
        assertEqualsJson(adder.create("discmania.json"), discmaniaResult);
    }

    @Test
    void countByCreatedAtBetweenTest() {
        var today = new Date();
        var yesterday = dateWithDayOffset(today, -1);
        var tomorrow = dateWithDayOffset(today, 1);
        var dayAfterTomorrow = dateWithDayOffset(today, 2);

        var insideRange = moldRepository.countByCreatedAtBetween(yesterday, tomorrow);
        var outsideRange = moldRepository.countByCreatedAtBetween(tomorrow, dayAfterTomorrow);

        assertEquals(4, insideRange);
        assertEquals(0, outsideRange);
    }

    private R_valm findManufacturer(String manufacturer) {
        return manufacturerRepository.findAll().stream()
                .filter(m -> manufacturer.equals(m.getValmistaja()))
                .findFirst()
                .orElseThrow();
    }
}
