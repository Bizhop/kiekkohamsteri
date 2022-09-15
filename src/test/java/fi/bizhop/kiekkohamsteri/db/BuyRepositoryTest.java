package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.Disc;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.model.Buy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Map;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.REPOSITORY;
import static fi.bizhop.kiekkohamsteri.TestUtils.*;
import static fi.bizhop.kiekkohamsteri.model.Buy.Status.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class BuyRepositoryTest extends SpringContextTestBase {
    @Autowired UserRepository userRepository;
    @Autowired DiscRepository discRepository;
    @Autowired ManufacturerRepository manufacturerRepository;
    @Autowired MoldRepository moldRepository;
    @Autowired ColorRepository colorRepository;
    @Autowired PlasticRepository plasticRepository;
    @Autowired GroupRepository groupRepository;

    @Autowired BuyRepository buyRepository;

    BaseAdder adder = new BaseAdder("buy", REPOSITORY);

    static Map<String, Object> testDataContainer;

    @BeforeEach
    void setupTestData() {
        //kind of dirty hack to make this run only once with autowired repos
        if(testDataContainer == null) {
            testDataContainer = initBaseData(userRepository, discRepository, manufacturerRepository, moldRepository, colorRepository, plasticRepository, groupRepository);

            var testDisc = (Disc) testDataContainer.get(TEST_DISC_KEY);
            var testUser = (User) testDataContainer.get(TEST_USER_KEY);
            var otherUser = (User) testDataContainer.get(OTHER_USER_KEY);
            var otherDisc = (Disc) testDataContainer.get(OTHER_DISC_KEY);

            buyRepository.deleteAll();
            buyRepository.save(new Buy(
                    testDisc,
                    testUser, //seller
                    otherUser, //buyer
                    REQUESTED));

            buyRepository.save(new Buy(
                    otherDisc,
                    otherUser, //seller
                    testUser, //buyer
                    REQUESTED));

            buyRepository.save(new Buy(
                    otherDisc,
                    otherUser, //seller
                    testUser, //buyer
                    CONFIRMED));
        }
    }

    @Test
    void findAllTest() {
        var all = buyRepository.findAll();

        assertEqualsJson(adder.create("1-2-3.json"), all);
    }

    @Test
    void findByStatusTest() {
        var requested = buyRepository.findByStatus(REQUESTED);
        var confirmed = buyRepository.findByStatus(CONFIRMED);
        var rejected = buyRepository.findByStatus(REJECTED);

        assertEqualsJson(adder.create("1-2.json"), requested);
        assertEqualsJson(adder.create("3.json"), confirmed);
        assertEqualsJson("expected/emptyArray.json", rejected);
    }

    @Test
    void findByDiscTest() {
        var byTestDisc = buyRepository.findByDisc((Disc)testDataContainer.get(TEST_DISC_KEY));
        var byOtherDisc = buyRepository.findByDisc((Disc)testDataContainer.get(OTHER_DISC_KEY));

        assertEqualsJson(adder.create("1.json"), byTestDisc);
        assertEqualsJson(adder.create("2-3.json"), byOtherDisc);
    }

    @Test
    void findByStatusAndSellerTest() {
        var testUser = (User) testDataContainer.get("testUser");
        var testSellerRequested = buyRepository.findByStatusAndSeller(REQUESTED, testUser);
        var testSellerConfirmed = buyRepository.findByStatusAndSeller(CONFIRMED, testUser);
        var testSellerRejected = buyRepository.findByStatusAndSeller(REJECTED, testUser);

        var otherUser = (User) testDataContainer.get("otherUser");
        var otherSellerRequested = buyRepository.findByStatusAndSeller(REQUESTED, otherUser);
        var otherSellerConfirmed = buyRepository.findByStatusAndSeller(CONFIRMED, otherUser);
        var otherSellerRejected = buyRepository.findByStatusAndSeller(REJECTED, otherUser);

        assertEqualsJson(adder.create("1.json"), testSellerRequested);
        assertEqualsJson("expected/emptyArray.json", testSellerConfirmed);
        assertEqualsJson("expected/emptyArray.json", testSellerRejected);

        assertEqualsJson(adder.create("2.json"), otherSellerRequested);
        assertEqualsJson(adder.create("3.json"), otherSellerConfirmed);
        assertEqualsJson("expected/emptyArray.json", otherSellerRejected);
    }

    @Test
    void findByStatusAndBuyerTest() {
        var testUser = (User) testDataContainer.get(TEST_USER_KEY);
        var testBuyerRequested = buyRepository.findByStatusAndBuyer(REQUESTED, testUser);
        var testBuyerConfirmed = buyRepository.findByStatusAndBuyer(CONFIRMED, testUser);
        var testBuyerRejected = buyRepository.findByStatusAndBuyer(REJECTED, testUser);

        var otherUser = (User) testDataContainer.get(OTHER_USER_KEY);
        var otherBuyerRequested = buyRepository.findByStatusAndBuyer(REQUESTED, otherUser);
        var otherBuyerConfirmed = buyRepository.findByStatusAndBuyer(CONFIRMED, otherUser);
        var otherBuyerRejected = buyRepository.findByStatusAndBuyer(REJECTED, otherUser);

        assertEqualsJson(adder.create("2.json"), testBuyerRequested);
        assertEqualsJson(adder.create("3.json"), testBuyerConfirmed);
        assertEqualsJson("expected/emptyArray.json", testBuyerRejected);

        assertEqualsJson(adder.create("1.json"), otherBuyerRequested);
        assertEqualsJson("expected/emptyArray.json", otherBuyerConfirmed);
        assertEqualsJson("expected/emptyArray.json", otherBuyerRejected);
    }

    @Test
    void findByDiscAndBuyerAndStatusTest() {
        var otherUser = (User) testDataContainer.get(OTHER_USER_KEY);
        var testDisc = (Disc) testDataContainer.get(TEST_DISC_KEY);

        var shouldBeThere = buyRepository.findByDiscAndBuyerAndStatus(testDisc, otherUser, REQUESTED);
        var shouldNotBeThere = buyRepository.findByDiscAndBuyerAndStatus(testDisc, otherUser, CONFIRMED);

        assertNotNull(shouldBeThere);
        assertNull(shouldNotBeThere);
    }

    @Test
    void countByUpdatedAtBetweenAndStatusTest() {
        var now = new Date();

        var tomorrow = dateWithDayOffset(now, 1);
        var afterTwoDays = dateWithDayOffset(now, 2);
        var yesterday = dateWithDayOffset(now, -1);

        var allRequested = buyRepository.countByUpdatedAtBetweenAndStatus(yesterday, tomorrow, REQUESTED);
        var outsideRange = buyRepository.countByUpdatedAtBetweenAndStatus(tomorrow, afterTwoDays, REQUESTED);

        assertEquals(2, allRequested);
        assertEquals(0, outsideRange);
    }
}
