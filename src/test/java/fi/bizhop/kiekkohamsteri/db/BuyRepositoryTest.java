package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.model.Ostot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Map;

import static fi.bizhop.kiekkohamsteri.TestUtils.*;
import static fi.bizhop.kiekkohamsteri.model.Ostot.Status.*;
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

    @Autowired BuyRepository buyRepository;

    BaseAdder adder = new BaseAdder("expected/repository/buy/");

    static Map<String, Object> testDataContainer;

    @BeforeEach
    void setupTestData() {
        //kind of dirty hack to make this run only once with autowired repos
        if(testDataContainer == null) {
            testDataContainer = initBaseData(userRepository, discRepository, manufacturerRepository, moldRepository, colorRepository, plasticRepository);

            var testDisc = (Kiekot) testDataContainer.get(TEST_DISC_KEY);
            var testUser = (Members) testDataContainer.get(TEST_USER_KEY);
            var otherUser = (Members) testDataContainer.get(OTHER_USER_KEY);
            var otherDisc = (Kiekot) testDataContainer.get(OTHER_DISC_KEY);

            buyRepository.deleteAll();
            buyRepository.save(new Ostot(
                    testDisc,
                    testUser, //seller
                    otherUser, //buyer
                    REQUESTED));

            buyRepository.save(new Ostot(
                    otherDisc,
                    otherUser, //seller
                    testUser, //buyer
                    REQUESTED));

            buyRepository.save(new Ostot(
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
    void findByKiekkoTest() {
        var byTestDisc = buyRepository.findByKiekko((Kiekot)testDataContainer.get(TEST_DISC_KEY));
        var byOtherDisc = buyRepository.findByKiekko((Kiekot)testDataContainer.get(OTHER_DISC_KEY));

        assertEqualsJson(adder.create("1.json"), byTestDisc);
        assertEqualsJson(adder.create("2-3.json"), byOtherDisc);
    }

    @Test
    void findByStatusAndMyyjaTest() {
        var testUser = (Members) testDataContainer.get("testUser");
        var testSellerRequested = buyRepository.findByStatusAndMyyja(REQUESTED, testUser);
        var testSellerConfirmed = buyRepository.findByStatusAndMyyja(CONFIRMED, testUser);
        var testSellerRejected = buyRepository.findByStatusAndMyyja(REJECTED, testUser);

        var otherUser = (Members) testDataContainer.get("otherUser");
        var otherSellerRequested = buyRepository.findByStatusAndMyyja(REQUESTED, otherUser);
        var otherSellerConfirmed = buyRepository.findByStatusAndMyyja(CONFIRMED, otherUser);
        var otherSellerRejected = buyRepository.findByStatusAndMyyja(REJECTED, otherUser);

        assertEqualsJson(adder.create("1.json"), testSellerRequested);
        assertEqualsJson("expected/emptyArray.json", testSellerConfirmed);
        assertEqualsJson("expected/emptyArray.json", testSellerRejected);

        assertEqualsJson(adder.create("2.json"), otherSellerRequested);
        assertEqualsJson(adder.create("3.json"), otherSellerConfirmed);
        assertEqualsJson("expected/emptyArray.json", otherSellerRejected);
    }

    @Test
    void findByStatusAndOstajaTest() {
        var testUser = (Members) testDataContainer.get(TEST_USER_KEY);
        var testBuyerRequested = buyRepository.findByStatusAndOstaja(REQUESTED, testUser);
        var testBuyerConfirmed = buyRepository.findByStatusAndOstaja(CONFIRMED, testUser);
        var testBuyerRejected = buyRepository.findByStatusAndOstaja(REJECTED, testUser);

        var otherUser = (Members) testDataContainer.get(OTHER_USER_KEY);
        var otherBuyerRequested = buyRepository.findByStatusAndOstaja(REQUESTED, otherUser);
        var otherBuyerConfirmed = buyRepository.findByStatusAndOstaja(CONFIRMED, otherUser);
        var otherBuyerRejected = buyRepository.findByStatusAndOstaja(REJECTED, otherUser);

        assertEqualsJson(adder.create("2.json"), testBuyerRequested);
        assertEqualsJson(adder.create("3.json"), testBuyerConfirmed);
        assertEqualsJson("expected/emptyArray.json", testBuyerRejected);

        assertEqualsJson(adder.create("1.json"), otherBuyerRequested);
        assertEqualsJson("expected/emptyArray.json", otherBuyerConfirmed);
        assertEqualsJson("expected/emptyArray.json", otherBuyerRejected);
    }

    @Test
    void findByKiekkoAndOstajaAndStatusTest() {
        var otherUser = (Members) testDataContainer.get(OTHER_USER_KEY);
        var testDisc = (Kiekot) testDataContainer.get(TEST_DISC_KEY);

        var shouldBeThere = buyRepository.findByKiekkoAndOstajaAndStatus(testDisc, otherUser, REQUESTED);
        var shouldNotBeThere = buyRepository.findByKiekkoAndOstajaAndStatus(testDisc, otherUser, CONFIRMED);

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
