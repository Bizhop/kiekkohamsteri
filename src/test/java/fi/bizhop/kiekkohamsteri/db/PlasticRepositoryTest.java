package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.R_muovi;
import fi.bizhop.kiekkohamsteri.model.R_valm;
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

        var innova = manufacturerRepository.save(new R_valm(null, "Innova"));
        var discmania = manufacturerRepository.save(new R_valm(null, "Discmania"));

        plasticRepository.save(new R_muovi(null, innova, "Champion"));
        plasticRepository.save(new R_muovi(null, innova, "Star"));
        plasticRepository.save(new R_muovi(null, discmania, "C-Line"));
        plasticRepository.save(new R_muovi(null, discmania, "S-Line"));
    }

    @Test
    void findAllByOrderByMuoviAscTest() {
        var result = plasticRepository.findAllByOrderByMuoviAsc();

        assertEqualsJson(adder.create("allDD.json"), result);
    }
}
