package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.Plastic;
import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.REPOSITORY;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;

@SpringBootTest
@ActiveProfiles("test")
public class PlasticRepositoryTest extends SpringContextTestBase {
    @Autowired ManufacturerRepository manufacturerRepository;

    @Autowired PlasticRepository plasticRepository;

    BaseAdder adder = new BaseAdder("plastic", REPOSITORY);

    @BeforeEach
    void setupTestData() {
        plasticRepository.deleteAll();
        manufacturerRepository.deleteAll();

        var innova = manufacturerRepository.save(new Manufacturer(null, "Innova"));
        var discmania = manufacturerRepository.save(new Manufacturer(null, "Discmania"));

        plasticRepository.save(new Plastic(null, innova, "Champion"));
        plasticRepository.save(new Plastic(null, innova, "Star"));
        plasticRepository.save(new Plastic(null, discmania, "C-Line"));
        plasticRepository.save(new Plastic(null, discmania, "S-Line"));
    }

    @Test
    void findAllByOrderByNameAscTest() {
        var result = plasticRepository.findAllByOrderByNameAsc();

        assertEqualsJson(adder.create("all.json"), result);
    }
}
