package fi.bizhop.kiekkohamsteri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.bizhop.kiekkohamsteri.db.*;
import fi.bizhop.kiekkohamsteri.model.*;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static fi.bizhop.kiekkohamsteri.TestObjects.OTHER_USER;
import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_USER;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    //map keys
    public static final String TEST_USER_KEY = "testUser";
    public static final String OTHER_USER_KEY = "otherUser";
    public static final String MANUFACTURER_KEY = "manufacturer";
    public static final String MOLD_KEY = "mold";
    public static final String COLOR_KEY = "color";
    public static final String PLASTIC_KEY = "plastic";
    public static final String TEST_DISC_KEY = "testDisc";
    public static final String OTHER_DISC_KEY = "otherDisc";

    public static void assertEqualsJson(String filename, String content) {
        var expected = readFile(filename);
        assertEqualsJsonInternal(expected, content);
    }

    public static void assertEqualsJson(String filename, Object contentObject) {
        var content = toJson(contentObject);
        var expected = readFile(filename);
        assertEqualsJsonInternal(expected, content);
    }

    private static void assertEqualsJsonInternal(String expected, String content) {
        try {
            JSONAssert.assertEquals(expected, content, false);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("Failed to read json");
        }
    }

    private static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("Failed to write json");
            return null;
        }
    }

    private static String readFile(String filename) {
        try {
            return FileUtils.readFileToString(new File(String.format("src/test/resources/%s", filename)), UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to read file");
            return null;
        }
    }

    public static Map<String, Object> initBaseData(UserRepository userRepository, DiscRepository discRepository, ManufacturerRepository manufacturerRepository, MoldRepository moldRepository, ColorRepository colorRepository, PlasticRepository plasticRepository) {
        discRepository.deleteAll();
        userRepository.deleteAll();
        moldRepository.deleteAll();
        colorRepository.deleteAll();
        plasticRepository.deleteAll();
        manufacturerRepository.deleteAll();

        var testUser = userRepository.save(TEST_USER);
        var otherUser = userRepository.save(OTHER_USER);

        var manufacturer = manufacturerRepository.save(new Manufacturer(null, "Test manufacturer"));
        var mold = moldRepository.save(new Mold(null, manufacturer, "Test mold", 0.0, 0.0, 0.0, 0.0));
        var color = colorRepository.save(new Color(null, "Test color"));
        var plastic = plasticRepository.save(new Plastic(null, manufacturer, "Test plastic"));

        var testDisc = discRepository.save(new Disc(testUser, mold, plastic, color));
        var otherDisc = discRepository.save(new Disc(otherUser, mold, plastic, color));

        var response = new HashMap<String, Object>();
        response.put(TEST_USER_KEY, testUser);
        response.put(OTHER_USER_KEY, otherUser);
        response.put(MANUFACTURER_KEY, manufacturer);
        response.put(MOLD_KEY, mold);
        response.put(COLOR_KEY, color);
        response.put(PLASTIC_KEY, plastic);
        response.put(TEST_DISC_KEY, testDisc);
        response.put(OTHER_DISC_KEY, otherDisc);
        return response;
    }

    public static Date dateWithDayOffset(Date input, int dayOffset) {
        var cal = Calendar.getInstance();
        cal.setTime(input);
        cal.add(Calendar.DATE, dayOffset);
        return cal.getTime();
    }
}
