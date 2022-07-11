package fi.bizhop.kiekkohamsteri.controller;

import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_USER;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerTest extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;
    @MockBean AuthService authService;

    @Test
    void givenUnableToAuthenticateUser_whenLogin_thenResponseCodeUnauthorized() {
        when(authService.login(any())).thenReturn(null);

        var response = restTemplate.getForEntity(String.format("%s%s", baseUrl(), "login"), String.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenAuthenticatedUser_whenLogin_thenRespondWithUserAndCodeOk() {
        when(authService.login(any())).thenReturn(TEST_USER);

        var response = restTemplate.getForEntity(String.format("%s%s", baseUrl(), "login"), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson("expectedTestUser.json", response.getBody());
    }

    private String baseUrl() {
        return String.format("http://localhost:%d/api/auth/", port);
    }
}
