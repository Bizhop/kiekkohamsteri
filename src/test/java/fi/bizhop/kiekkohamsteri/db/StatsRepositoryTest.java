package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.REPOSITORY;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
public class StatsRepositoryTest extends SpringContextTestBase {
    @Autowired StatsRepository statsRepository;

    BaseAdder adder = new BaseAdder("stats", REPOSITORY);

    @BeforeEach
    void setupTestData() {
        statsRepository.deleteAll();

        statsRepository.save(new Stats(null, 2020, 6, 11,22,33,44,55,66));
        statsRepository.save(new Stats(null, 2021, 5, 66,55,44,33,22,11));
    }

    @Test
    void findAllTest() {
        var result = statsRepository.findAll(Pageable.unpaged());

        assertEqualsJson(adder.create("all.json"), result);
    }

    @Test
    void findByYearAndMonth() {
        var shouldHaveStats = statsRepository.findByYearAndMonth(2021, 5);
        var shouldNotHaveStats = statsRepository.findByYearAndMonth(2022, 3);

        assertEqualsJson(adder.create("2021-5.json"), shouldHaveStats);
        assertNull(shouldNotHaveStats);
    }
}
