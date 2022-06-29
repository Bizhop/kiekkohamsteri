package fi.bizhop.kiekkohamsteri.controller;

import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PingControllerTest extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;

    @Test
    void pingTest() {
        var response = restTemplate.getForObject(String.format("%s%s", baseUrl(), "ping"), String.class);

        assertEquals("pong", response);
    }

    private String baseUrl() {
        return String.format("http://localhost:%d/api/", port);
    }
}
