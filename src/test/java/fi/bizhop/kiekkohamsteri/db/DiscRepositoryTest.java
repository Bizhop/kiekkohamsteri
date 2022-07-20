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

            var testUser = (User) testDataContainer.get(TEST_USER_KEY);
            var otherUser = (User) testDataContainer.get(OTHER_USER_KEY);

            addDiscsFor(testUser, 4, false, false, false);
            addDiscsFor(testUser, 1, false, false, true);
            addDiscsFor(otherUser, 5, false, true, false);
            addDiscsFor(otherUser, 2, true, false, false);
        }
    }

    private void addDiscsFor(User user, int numberOfDiscs, boolean lost, boolean publicDisc, boolean forSale) {
        var mold = (Mold) testDataContainer.get(MOLD_KEY);
        var plastic = (Plastic) testDataContainer.get(PLASTIC_KEY);
        var color = (Color) testDataContainer.get(COLOR_KEY);

        var newDiscs = IntStream.range(0, numberOfDiscs)
                .mapToObj(i -> new Disc(user, mold, plastic, color))
                .peek(disc -> {
                    disc.setLost(lost);
                    disc.setPublicDisc(publicDisc);
                    disc.setForSale(forSale);
                })
                .collect(Collectors.toList());
        discRepository.saveAll(newDiscs);
    }

    @Test
    void findByOwnerAndLostFalseTest() {
        var user = (User) testDataContainer.get(TEST_USER_KEY);
        var result = discRepository.findByOwnerAndLostFalse(user, Pageable.unpaged());

        assertEqualsJson(adder.create("byMemberAndLostFalse.json"), result);
    }

    @Test
    void findByOwnerInAndPublicDiscTrueTest() {
        var user = (User) testDataContainer.get(TEST_USER_KEY);
        var other = (User) testDataContainer.get(OTHER_USER_KEY);
        var result = discRepository.findByOwnerInAndPublicDiscTrue(List.of(user, other));

        assertEqualsJson(adder.create("byMemberInAndPublicDiscTrue.json"), result);
    }

    @Test
    void findByLostTrueTest() {
        var result = discRepository.findByLostTrue(Pageable.unpaged());

        assertEqualsJson(adder.create("byLostTrue.json"), result);
    }

    @Test
    void getDiscByIdTest() {
        var disc = discRepository.findAll().iterator().next();
        var result = discRepository.getDiscById(disc.getId());

        assertEqualsJson(adder.create("byId.json"), result);
    }

    @Test
    void findByForSaleTrueTest() {
        var result = discRepository.findByForSaleTrue(Pageable.unpaged());

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
        var testUser = (User) testDataContainer.get(TEST_USER_KEY);
        var otherUser = (User) testDataContainer.get(OTHER_USER_KEY);

        var testUserDiscs = discRepository.countByOwner(testUser);
        var otherUserDiscs = discRepository.countByOwner(otherUser);

        assertEquals(5, testUserDiscs);
        assertEquals(7, otherUserDiscs);
    }
}
