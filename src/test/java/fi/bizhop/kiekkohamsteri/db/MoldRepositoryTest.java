package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.Mold;
import fi.bizhop.kiekkohamsteri.model.Manufacturer;
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

        var innova = manufacturerRepository.save(new Manufacturer(null, "Innova"));
        var discmania = manufacturerRepository.save(new Manufacturer(null, "Discmania"));

        moldRepository.save(new Mold(null, discmania, "PD", 10.0, 4.0, 0.0, 3.0));
        moldRepository.save(new Mold(null, discmania, "FD", 7.0, 6.0, -1.0, 1.0));
        moldRepository.save(new Mold(null, innova, "Destroyer", 12.0, 5.0, -1.0, 3.0));
        moldRepository.save(new Mold(null, innova, "Roc", 4.0, 4.0, 0.0, 3.0));
    }

    @Test
    void findAllByOrderByNameAscTest() {
        var result = moldRepository.findAllByOrderByNameAsc();

        assertEqualsJson(adder.create("allDD.json"), result);
    }

    @Test
    void findByManufacturerOrderByNameAscTest() {
        var innovaResult = moldRepository.findByManufacturerOrderByNameAsc(findManufacturer("Innova"));
        var discmaniaResult = moldRepository.findByManufacturerOrderByNameAsc(findManufacturer("Discmania"));

        assertEqualsJson(adder.create("innovaDD.json"), innovaResult);
        assertEqualsJson(adder.create("discmaniaDD.json"), discmaniaResult);
    }

    @Test
    void findByManufacturerTest() {
        var innovaResult = moldRepository.findByManufacturer(findManufacturer("Innova"), Pageable.unpaged());
        var discmaniaResult = moldRepository.findByManufacturer(findManufacturer("Discmania"), Pageable.unpaged());

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

    private Manufacturer findManufacturer(String manufacturer) {
        return manufacturerRepository.findAll().stream()
                .filter(m -> manufacturer.equals(m.getName()))
                .findFirst()
                .orElseThrow();
    }
}
