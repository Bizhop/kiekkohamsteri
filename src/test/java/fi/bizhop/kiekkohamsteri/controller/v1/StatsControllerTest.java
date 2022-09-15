package fi.bizhop.kiekkohamsteri.controller.v1;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.Stats;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.StatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.CONTROLLER;
import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_USER;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StatsControllerTest  extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;
    @MockBean AuthService authService;
    @MockBean StatsService statsService;

    BaseAdder adder = new BaseAdder("stats", CONTROLLER);

    @Test
    void givenUnableToAuthenticateUser_whenGetStats_thenResponseCodeUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(), String.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenValidUser_whenGetStats_thenGetStats() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var stats = new Stats(null, 2020, 1, 11, 33, 44, 55, 22 ,66);

        when(statsService.getStats(any())).thenReturn(new PageImpl<>(List.of(stats)));

        var response = restTemplate.getForEntity(createUrl(), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("stats.json"), response.getBody());
    }


    //HELPER METHODS

    private String createUrl() {
        return String.format("http://localhost:%d/api/stats", port);
    }
}
