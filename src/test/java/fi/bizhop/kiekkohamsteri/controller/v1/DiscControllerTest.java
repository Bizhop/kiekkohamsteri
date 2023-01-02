package fi.bizhop.kiekkohamsteri.controller.v1;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscInputDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.Buy;
import fi.bizhop.kiekkohamsteri.model.User;
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

import java.time.Clock;
import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.CONTROLLER;
import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
    @MockBean MoldService moldService;
    @MockBean PlasticService plasticService;
    @MockBean ColorService colorService;
    @MockBean Clock clock;
    @MockBean UserService userService;

    BaseAdder adder = new BaseAdder("disc", CONTROLLER);

    @ParameterizedTest
    @ValueSource(strings = {"", "myytavat", "1", "lost"})
    void givenUnableToAuthenticateUser_whenCallingGetEndpoint_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(endpoint), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1/buy"})
    void givenUnableToAuthenticateUser_whenCallingPostEndpoint_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.postForEntity(createUrl(endpoint), null, Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1/found"})
    void givenUnableToAuthenticateUser_whenCallingPatchEndpoint_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.exchange(createUrl(endpoint), PATCH, null, Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void givenUnableToAuthenticateUser_whenCallingDeleteEndpoint_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.exchange(createUrl(endpoint), DELETE, null, Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenUnableToAuthenticateUser_whenCallingUpdateDisc_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var requestEntity = new HttpEntity<>(DiscInputDto.builder().build());
        var response = restTemplate.exchange(createUrl("1"), PUT, requestEntity, String.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenValidRequest_whenDeleteDisc_thenDeleteDisc() throws AuthorizationException {
        var user = new User(TEST_EMAIL);
        when(authService.getUser(any())).thenReturn(user);

        var response = restTemplate.exchange(createUrl("123"), DELETE, null, Object.class);

        verify(discService, times(1)).deleteDisc(123L, user);

        assertEquals(SC_NO_CONTENT, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenNotYourDiscOrDiscNotFound_whenDeleteDisc_thenRespondUnauthorized() throws AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        doThrow(new AuthorizationException()).when(discService).deleteDisc(123L, TEST_USER);

        var response = restTemplate.exchange(createUrl("123"), DELETE, null, Object.class);

        verify(discService, times(1)).deleteDisc(123L, TEST_USER);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenValidRequest_whenBuyDisc_thenBuyDisc() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        var disc = getTestDiscFor(OTHER_USER);
        when(discService.getDiscDb(123L)).thenReturn(Optional.of(disc));

        var buys = new Buy(disc, disc.getOwner(), TEST_USER, Buy.Status.REQUESTED);
        when(buyService.buyDisc(TEST_USER, disc)).thenReturn(buys);

        var response = restTemplate.postForEntity(createUrl("123/buy"), null, String.class);
        System.out.println(response.getBody());

        verify(discService, times(1)).getDiscDb(123L);
        verify(buyService, times(1)).buyDisc(TEST_USER, disc);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("buyDiscRequest.json"), response.getBody());
    }

    @Test
    void givenDiscNotFoundOrNotForSale_whenBuyDisc_thenRespondForbidden() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(discService.getDiscDb(456L)).thenReturn(Optional.empty());
        doThrow(new HttpResponseException(SC_FORBIDDEN, "Not for sale")).when(buyService).buyDisc(TEST_USER, null);

        var response = restTemplate.postForEntity(createUrl("456/buy"), null, String.class);

        verify(discService, times(1)).getDiscDb(456L);
        verify(buyService, times(1)).buyDisc(TEST_USER, null);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenAlreadyBuyingDisc_whenBuyDisc_thenRespondBadRequest() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        var disc = getTestDiscFor(OTHER_USER);
        when(discService.getDiscDb(123L)).thenReturn(Optional.of(disc));
        when(buyService.buyDisc(TEST_USER, disc)).thenThrow(new HttpResponseException(SC_BAD_REQUEST, "You are already buying this disc"));

        var response = restTemplate.postForEntity(createUrl("123/buy"), null, String.class);

        verify(discService, times(1)).getDiscDb(123L);
        verify(buyService, times(1)).buyDisc(TEST_USER, disc);

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenYourOwnDisc_whenBuyDisc_thenRespondBadRequest() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        var disc = getTestDiscFor(TEST_USER);
        when(discService.getDiscDb(123L)).thenReturn(Optional.of(disc));
        when(buyService.buyDisc(TEST_USER, disc)).thenThrow(new HttpResponseException(SC_BAD_REQUEST, "You can't buy your own disc"));

        var response = restTemplate.postForEntity(createUrl("123/buy"), null, String.class);

        verify(discService, times(1)).getDiscDb(123L);
        verify(buyService, times(1)).buyDisc(TEST_USER, disc);

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenValidRequest_whenMarkFound_thenHandleFoundDisc() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var response = restTemplate.exchange(createUrl("123/found"), PATCH, null, Object.class);

        verify(discService, times(1)).handleFoundDisc(TEST_USER, 123L);

        assertEquals(SC_NO_CONTENT, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenNotYourDiscOrDiscNotExisting_whenMarkFound_thenRespondForbidden() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        doThrow(new HttpResponseException(SC_FORBIDDEN, "User is not disc owner")).when(discService).handleFoundDisc(TEST_USER, 123L);

        var response = restTemplate.exchange(createUrl("123/found"), PATCH, null, Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenDiscIsNotLost_whenMarkFound_thenRespondBadRequest() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        doThrow(new HttpResponseException(SC_BAD_REQUEST, "Disc is not lost")).when(discService).handleFoundDisc(TEST_USER, 123L);

        var response = restTemplate.exchange(createUrl("123/found"), PATCH, null, Object.class);

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }


    //HELPER METHODS

    private String createUrl(String endpoint) {
        return String.format("http://localhost:%d/api/kiekot/%s", port, endpoint);
    }
}
