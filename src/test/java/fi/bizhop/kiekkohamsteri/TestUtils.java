package fi.bizhop.kiekkohamsteri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TestUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode getJsonFromFile(String filename) throws IOException {
        var string = FileUtils.readFileToString(new File(String.format("src/test/resources/%s", filename)), UTF_8);
        return mapper.readTree(string);
    }

    public static JsonNode getJsonFromString(String content) throws JsonProcessingException {
        return mapper.readTree(content);
    }
}
