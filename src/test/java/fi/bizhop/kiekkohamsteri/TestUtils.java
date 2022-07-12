package fi.bizhop.kiekkohamsteri;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {
    public static void assertEqualsJson(String filename, String content) {
        try {
            var expectedString = FileUtils.readFileToString(new File(String.format("src/test/resources/%s", filename)), UTF_8);
            JSONAssert.assertEquals(expectedString, content, false);
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
            fail("Failed to read json");
        }
    }
}
