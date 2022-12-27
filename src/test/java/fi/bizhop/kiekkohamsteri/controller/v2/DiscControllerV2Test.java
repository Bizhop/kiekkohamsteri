package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscSearchDto;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.DiscService;
import fi.bizhop.kiekkohamsteri.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.CONTROLLER;
import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_USER;
import static fi.bizhop.kiekkohamsteri.TestObjects.getDiscsByUserV2;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DiscControllerV2Test extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;

    @MockBean AuthService authService;
    @MockBean UserService userService;
    @MockBean DiscService discService;

    BaseAdder adder = new BaseAdder("disc/v2", CONTROLLER);

    @ParameterizedTest
    @ValueSource(strings = {""})
    void givenUnableToAuthenticateUser_whenCallingGetEndpoints_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(endpoint), String.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void getDiscsTest() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var discs = getDiscsByUserV2(TEST_USER);
        when(discService.getDiscsV2(eq(TEST_USER), any())).thenReturn(new PageImpl<>(discs));

        var response = restTemplate.getForEntity(createUrl(""), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("getDiscs.json"), response.getBody());
    }

    @Test
    void searchTest() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var discs = getDiscsByUserV2(TEST_USER);
        var searchDto = DiscSearchDto.builder().build();
        when(discService.search(eq(TEST_USER), any(), eq(searchDto))).thenReturn(new PageImpl<>(discs));

        var response = restTemplate.postForEntity(createUrl("search"), searchDto, String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("getDiscs.json"), response.getBody());
    }

    @Test
    void supportedOperationsTest() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var response = restTemplate.getForEntity(createUrl("search"), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("supportedOperations.json"), response.getBody());
    }

    private String createUrl(String endpoint) {
        return String.format("http://localhost:%d/api/v2/discs/%s", port, endpoint);
    }
}
