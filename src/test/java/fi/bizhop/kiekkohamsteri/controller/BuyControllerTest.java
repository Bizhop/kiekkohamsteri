package fi.bizhop.kiekkohamsteri.controller;

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

import static fi.bizhop.kiekkohamsteri.TestObjects.DISCS;
import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_USER;
import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.*;
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

    @ParameterizedTest
    @ValueSource(strings = {"", "omat"})
    void givenUnableToAuthenticateUser_whenCallingGetEndpoint_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(endpoint), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1/confirm", "1/reject"})
    void givenUnableToAuthenticateUser_whenCallingPostEndpoint_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.postForEntity(createUrl(endpoint), null, Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    @SuppressWarnings({"unchecked"})
    void givenValidUser_whenGetListing_thenReturnListing() {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(buyService.getListing(null)).thenReturn(List.of(new Ostot(), new Ostot()));

        var response = restTemplate.getForEntity(createUrl(""), List.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        var buys = (List<Ostot>) response.getBody();
        assertNotNull(buys);
        assertEquals(2, buys.size());
    }

    @Test
    void givenValidUser_whenGetSummary_thenReturnSummary() {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        var buys = BuysDto.builder()
                .myyjana(List.of(new Ostot()))
                .ostajana(List.of(new Ostot(), new Ostot()))
                .build();
        when(buyService.getSummary(TEST_USER)).thenReturn(buys);

        var response = restTemplate.getForEntity(createUrl("omat"), BuysDto.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        var summary = response.getBody();
        assertNotNull(summary);
        assertEquals(1, summary.getMyyjana().size());
        assertEquals(2, summary.getOstajana().size());
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
