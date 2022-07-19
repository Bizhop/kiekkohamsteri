package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.REPOSITORY;
import static fi.bizhop.kiekkohamsteri.TestObjects.OTHER_EMAIL;
import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_EMAIL;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static fi.bizhop.kiekkohamsteri.TestUtils.dateWithDayOffset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest extends SpringContextTestBase {
    @Autowired UserRepository userRepository;

    BaseAdder adder = new BaseAdder("user", REPOSITORY);

    @BeforeEach
    void setupTestData() {
        userRepository.deleteAll();

        var testUser = new User(TEST_EMAIL);
        var otherUser = new User(OTHER_EMAIL);

        testUser.setUsername("test user");
        testUser.setDiscCount(55);
        testUser.setPublicDiscCount(true);
        testUser.setPublicList(true);

        otherUser.setUsername("other user");
        otherUser.setDiscCount(88);
        otherUser.setPublicDiscCount(false);
        otherUser.setPublicList(false);

        userRepository.saveAll(List.of(testUser, otherUser));
    }

    @Test
    void findByEmailTest() {
        var testUser = userRepository.findByEmail(TEST_EMAIL);
        var otherUser = userRepository.findByEmail(OTHER_EMAIL);
        var none = userRepository.findByEmail("something.else@example.com");

        assertEquals(TEST_EMAIL, testUser.getEmail());
        assertEquals(OTHER_EMAIL, otherUser.getEmail());
        assertNull(none);
    }

    @Test
    void findByPublicDiscCountTrueOrderByDiscCountDescTest() {
        var result = userRepository.findByPublicDiscCountTrueOrderByDiscCountDesc();

        assertEqualsJson(adder.create("leaders.json"), result);
    }

    @Test
    void findByPublicListTrueTest() {
        var result = userRepository.findByPublicListTrue();

        assertEqualsJson(adder.create("publicListTrue.json"), result);
    }

    @Test
    void countByCreatedAtBetweenTest() {
        var today = new Date();
        var yesterday = dateWithDayOffset(today, -1);
        var tomorrow = dateWithDayOffset(today, 1);
        var dayAfterTomorrow = dateWithDayOffset(today, 2);

        var insideRange = userRepository.countByCreatedAtBetween(yesterday, tomorrow);
        var outsideRange = userRepository.countByCreatedAtBetween(tomorrow, dayAfterTomorrow);

        assertEquals(2, insideRange);
        assertEquals(0, outsideRange);
    }
}
