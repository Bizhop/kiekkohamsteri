package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.REPOSITORY;
import static fi.bizhop.kiekkohamsteri.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class DiscRepositoryTest extends SpringContextTestBase {
    @Autowired UserRepository userRepository;
    @Autowired DiscRepository discRepository;
    @Autowired ManufacturerRepository manufacturerRepository;
    @Autowired MoldRepository moldRepository;
    @Autowired ColorRepository colorRepository;
    @Autowired PlasticRepository plasticRepository;

    static Map<String, Object> testDataContainer = null;

    BaseAdder adder = new BaseAdder("disc", REPOSITORY);

    @BeforeEach
    void setupTestData() {
        //kind of dirty hack to make this run only once with autowired repos
        if(testDataContainer == null) {
            testDataContainer = initBaseData(userRepository, discRepository, manufacturerRepository, moldRepository, colorRepository, plasticRepository);
            discRepository.deleteAll();

            var testUser = (Members) testDataContainer.get(TEST_USER_KEY);
            var otherUser = (Members) testDataContainer.get(OTHER_USER_KEY);

            addDiscsFor(testUser, 4, false, false, false);
            addDiscsFor(testUser, 1, false, false, true);
            addDiscsFor(otherUser, 5, false, true, false);
            addDiscsFor(otherUser, 2, true, false, false);
        }
    }

    private void addDiscsFor(Members user, int numberOfDiscs, boolean lost, boolean publicDisc, boolean forSale) {
        var mold = (R_mold) testDataContainer.get(MOLD_KEY);
        var plastic = (R_muovi) testDataContainer.get(PLASTIC_KEY);
        var color = (R_vari) testDataContainer.get(COLOR_KEY);

        var newDiscs = IntStream.range(0, numberOfDiscs)
                .mapToObj(i -> new Kiekot(user, mold, plastic, color))
                .peek(disc -> {
                    disc.setLost(lost);
                    disc.setPublicDisc(publicDisc);
                    disc.setMyynnissa(forSale);
                })
                .collect(Collectors.toList());
        discRepository.saveAll(newDiscs);
    }

    @Test
    void findByMemberAndLostFalseTest() {
        var user = (Members) testDataContainer.get(TEST_USER_KEY);
        var result = discRepository.findByMemberAndLostFalse(user, Pageable.unpaged());

        assertEqualsJson(adder.create("byMemberAndLostFalse.json"), result);
    }

    @Test
    void findByMemberInAndPublicDiscTrueTest() {
        var user = (Members) testDataContainer.get(TEST_USER_KEY);
        var other = (Members) testDataContainer.get(OTHER_USER_KEY);
        var result = discRepository.findByMemberInAndPublicDiscTrue(List.of(user, other));

        assertEqualsJson(adder.create("byMemberInAndPublicDiscTrue.json"), result);
    }

    @Test
    void findByLostTrueTest() {
        var result = discRepository.findByLostTrue(Pageable.unpaged());

        assertEqualsJson(adder.create("byLostTrue.json"), result);
    }

    @Test
    void getKiekotByIdTest() {
        var disc = discRepository.findAll().iterator().next();
        var result = discRepository.getKiekotById(disc.getId());

        assertEqualsJson(adder.create("byId.json"), result);
    }

    @Test
    void findByMyynnissaTrueTest() {
        var result = discRepository.findByMyynnissaTrue(Pageable.unpaged());

        assertEqualsJson(adder.create("byMyynnissaTrue.json"), result);
    }

    @Test
    void countByCreatedAtBetweenTest() {
        var now = new Date();

        var tomorrow = dateWithDayOffset(now, 1);
        var afterTwoDays = dateWithDayOffset(now, 2);
        var yesterday = dateWithDayOffset(now, -1);

        var allCreated = discRepository.countByCreatedAtBetween(yesterday, tomorrow);
        var outsideRange = discRepository.countByCreatedAtBetween(tomorrow, afterTwoDays);

        assertEquals(12, allCreated);
        assertEquals(0, outsideRange);
    }

    @Test
    void countByMemberTest() {
        var testUser = (Members) testDataContainer.get(TEST_USER_KEY);
        var otherUser = (Members) testDataContainer.get(OTHER_USER_KEY);

        var testUserDiscs = discRepository.countByMember(testUser);
        var otherUserDiscs = discRepository.countByMember(otherUser);

        assertEquals(5, testUserDiscs);
        assertEquals(7, otherUserDiscs);
    }
}
