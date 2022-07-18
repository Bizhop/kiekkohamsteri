package fi.bizhop.kiekkohamsteri.controller;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.BuysDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Ostot;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.BuyService;
import fi.bizhop.kiekkohamsteri.service.DiscService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.CONTROLLER;
import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static fi.bizhop.kiekkohamsteri.model.Ostot.Status.REQUESTED;
import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BuyControllerTest extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;
    @MockBean BuyService buyService;
    @MockBean AuthService authService;
    @MockBean DiscService discService;

    @Captor
    ArgumentCaptor<Kiekot> discCaptor;

    BaseAdder adder = new BaseAdder("buy", CONTROLLER);

    @ParameterizedTest
    @ValueSource(strings = {"", "omat"})
    void givenUnableToAuthenticateUser_whenCallingGetEndpoint_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(endpoint), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1/confirm", "1/reject"})
    void givenUnableToAuthenticateUser_whenCallingPostEndpoint_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.postForEntity(createUrl(endpoint), null, Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenValidUser_whenGetListing_thenReturnListing() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var buy1 = new Ostot(getTestDiscFor(TEST_USER), TEST_USER, OTHER_USER, REQUESTED);
        var buy2 = new Ostot(getTestDiscFor(OTHER_USER), OTHER_USER, TEST_USER, REQUESTED);
        when(buyService.getListing(null)).thenReturn(List.of(buy1, buy2));

        var response = restTemplate.getForEntity(createUrl(""), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("buyListing.json"), response.getBody());
    }

    @Test
    void givenValidUser_whenGetSummary_thenReturnSummary() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var sell = new Ostot(getTestDiscFor(TEST_USER), TEST_USER, OTHER_USER, REQUESTED);
        var buy1 = new Ostot(getTestDiscFor(OTHER_USER), OTHER_USER, TEST_USER, REQUESTED);
        var buy2 = new Ostot(getTestDiscFor(OTHER_USER), OTHER_USER, TEST_USER, REQUESTED);
        var buys = BuysDto.builder()
                .myyjana(List.of(sell))
                .ostajana(List.of(buy1, buy2))
                .build();
        when(buyService.getSummary(TEST_USER)).thenReturn(buys);

        var response = restTemplate.getForEntity(createUrl("omat"), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("buySummary.json"), response.getBody());
    }

    @Test
    void givenAuthorizationException_whenConfirm_thenRespondWithForbidden() throws AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(buyService.confirm(123L, TEST_USER)).thenThrow(new AuthorizationException());

        var response = restTemplate.postForEntity(createUrl("123/confirm"), null, Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenResponseIsDisc_whenConfirm_thenSaveDisc() throws AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        var disc = DISCS.get(0);
        when(buyService.confirm(123L, TEST_USER)).thenReturn(disc);

        var response = restTemplate.postForEntity(createUrl("123/confirm"), null, Object.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(discService, times(1)).saveDisc(discCaptor.capture());
        var saved = discCaptor.getValue();
        assertEquals(disc, saved);
    }

    @Test
    void givenAuthorizationException_whenReject_thenRespondWithForbidden() throws AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        doThrow(new AuthorizationException()).when(buyService).reject(123L, TEST_USER);

        var response = restTemplate.postForEntity(createUrl("123/reject"), null, Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenNoException_whenReject_thenRespondWithOk() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var response = restTemplate.postForEntity(createUrl("123/reject"), null, Object.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    private String createUrl(String endpoint) {
        return String.format("http://localhost:%d/api/ostot/%s", port, endpoint);
    }
}
