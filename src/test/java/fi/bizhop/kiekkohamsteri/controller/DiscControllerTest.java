package fi.bizhop.kiekkohamsteri.controller;

import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.DiscDto;
import fi.bizhop.kiekkohamsteri.dto.UploadDto;
import fi.bizhop.kiekkohamsteri.projection.v1.DiscProjection;
import fi.bizhop.kiekkohamsteri.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DiscControllerTest extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;
    @MockBean DiscService discService;
    @MockBean AuthService authService;
    @MockBean UploadService uploadService;
    @MockBean BuyService buyService;
    @MockBean UserService userService;
    @MockBean MoldService moldService;
    @MockBean PlasticService plasticService;
    @MockBean ColorService colorService;

    @ParameterizedTest
    @ValueSource(strings = {"", "myytavat", "1", "public-lists", "lost"})
    void givenUnableToAuthenticateUser_whenCallingGetEndpoint_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(endpoint), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1/buy"})
    void givenUnableToAuthenticateUser_whenCallingPostEndpoint_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.postForEntity(createUrl(endpoint), null, Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1/found"})
    void givenUnableToAuthenticateUser_whenCallingPatchEndpoint_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.exchange(createUrl(endpoint), PATCH, null, Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void givenUnableToAuthenticateUser_whenCallingDeleteEndpoint_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.exchange(createUrl(endpoint), DELETE, null, Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenUnableToAuthenticateUser_whenCallingPostNewDisc_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.postForEntity(createUrl(""), UploadDto.builder().build(), DiscProjection.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenUnableToAuthenticateUser_whenCallingUpdateImage_thenRespondWithUnathorized() {
        when(authService.getUser(any())).thenReturn(null);

        var requestEntity = new HttpEntity<>(UploadDto.builder().build());
        var response = restTemplate.exchange(createUrl("1/update-image"), PATCH, requestEntity, Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenUnableToAuthenticateUser_whenCallingUpdateDisc_thenRespondWithUnathorized() {
        when(authService.getUser(any())).thenReturn(null);

        var requestEntity = new HttpEntity<>(DiscDto.builder().build());
        var response = restTemplate.exchange(createUrl("1"), PUT, requestEntity, DiscProjection.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    private String createUrl(String endpoint) {
        return String.format("http://localhost:%d/api/kiekot/%s", port, endpoint);
    }
}
