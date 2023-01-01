package fi.bizhop.kiekkohamsteri.controller.v1;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscInputDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.UploadDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.Buy;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.service.*;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
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

    @Captor ArgumentCaptor<Pageable> pageableCaptor;

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
    void givenUnableToAuthenticateUser_whenCallingPostNewDisc_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.postForEntity(createUrl(""), uploadDto().build(), String.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenUnableToAuthenticateUser_whenCallingUpdateImage_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var requestEntity = new HttpEntity<>(uploadDto().build());
        var response = restTemplate.exchange(createUrl("1/update-image"), PATCH, requestEntity, Object.class);

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
    void getDiscsForSaleTest() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var discProjections = getDiscsByUser(OTHER_USER);
        var page = new PageImpl<>(discProjections);
        when(discService.getDiscsForSale(any())).thenReturn(page);

        var response = restTemplate.getForEntity(createUrl("myytavat"), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("getDiscsForSale.json"), response.getBody());
    }

    @Test
    void givenImageUploadSuccess_whenCreateDisc_thenSaveDiscAndUpdateImageReference() throws IOException {
        var user = new User(TEST_EMAIL);
        when(authService.getUser(any())).thenReturn(user);
        whenDefaultMoldPlasticAndColor();

        var disc = getTestDiscFor(user);
        var discId = 123L;
        disc.setId(discId);
        var discProjection = projectionFromDisc(disc);

        var image = String.format("%s-%d", user.getUsername(), discId);

        when(discService.newDisc(user, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0))).thenReturn(discProjection);
        when(discService.updateImage(discId, image)).thenReturn(discProjection);

        var dto = uploadDto().build();

        var response = restTemplate.postForEntity(createUrl(""), dto, String.class);

        verify(discService, times(1)).newDisc(user, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0));
        verify(uploadService, times(1)).upload(dto, image);
        verify(discService, times(1)).updateImage(discId, image);

        assertEquals(SC_OK, response.getStatusCodeValue());

        assertEqualsJson(adder.create("newDisc.json"), response.getBody());
    }

    @Test
    void givenImageUploadFails_whenCreateDisc_thenDiscIsDeleted() throws IOException {
        var user = new User(TEST_EMAIL);
        when(authService.getUser(any())).thenReturn(user);
        whenDefaultMoldPlasticAndColor();

        var disc = getTestDiscFor(user);
        var discId = 123L;
        disc.setId(discId);
        var discProjection = projectionFromDisc(disc);

        var image = String.format("%s-%d", user.getUsername(), discId);

        when(discService.newDisc(user, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0))).thenReturn(discProjection);

        var dto = uploadDto().build();
        doThrow(new IOException()).when(uploadService).upload(dto, image);

        var response = restTemplate.postForEntity(createUrl(""), dto, String.class);

        verify(discService, times(1)).newDisc(user, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0));
        verify(uploadService, times(1)).upload(dto, image);
        verify(discService, times(1)).deleteDiscById(discId);

        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenNullImageData_whenCreateDisc_thenRespondBadRequest() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var dto = UploadDto.builder().build();

        var response = restTemplate.postForEntity(createUrl(""), dto, String.class);

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenValidRequest_whenUpdateImage_thenUpdateImage() throws AuthorizationException, IOException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var disc = getTestDiscFor(TEST_USER);
        var discId = 123L;
        disc.setId(discId);
        disc.setImage("Test-123");
        var discProjection = projectionFromDisc(disc);

        when(discService.getDisc(TEST_USER, 123L)).thenReturn(discProjection);
        when(clock.instant()).thenReturn(TEST_TIMESTAMP);

        var dto = uploadDto().build();
        var newImage = String.format("Test-123-%d", TEST_TIMESTAMP.toEpochMilli());

        var response = restTemplate.exchange(createUrl("123/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

        verify(discService, times(1)).getDisc(TEST_USER, 123L);
        verify(clock, times(1)).instant();
        verify(uploadService, times(1)).upload(dto, newImage);
        verify(discService, times(1)).updateImage(123L, newImage);

        assertEquals(SC_NO_CONTENT, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenImageUploadFails_whenUpdateImage_thenRespondInternalServerError() throws AuthorizationException, IOException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var disc = getTestDiscFor(TEST_USER);
        var discId = 123L;
        disc.setId(discId);
        disc.setImage("Test-123");
        var discProjection = projectionFromDisc(disc);

        when(discService.getDisc(TEST_USER, 123L)).thenReturn(discProjection);
        when(clock.instant()).thenReturn(TEST_TIMESTAMP);

        var dto = uploadDto().build();
        var newImage = String.format("Test-123-%d", TEST_TIMESTAMP.toEpochMilli());
        doThrow(new IOException()).when(uploadService).upload(dto, newImage);

        var response = restTemplate.exchange(createUrl("123/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

        verify(discService, times(1)).getDisc(TEST_USER, 123L);
        verify(clock, times(1)).instant();
        verify(uploadService, times(1)).upload(dto, newImage);
        verify(discService, never()).updateImage(anyLong(), anyString());

        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenNotYourDisc_whenUpdateImage_thenRespondForbidden() throws AuthorizationException, IOException {
        var user = new User(TEST_EMAIL);
        when(authService.getUser(any())).thenReturn(user);

        when(discService.getDisc(TEST_USER, 456L)).thenThrow(new AuthorizationException());

        var dto = uploadDto().build();

        var response = restTemplate.exchange(createUrl("456/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

        verify(clock, never()).instant();
        verify(uploadService, never()).upload(any(), anyString());
        verify(discService, never()).updateImage(anyLong(), anyString());

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenNullImageData_whenUpdateImage_thenRespondBadRequest() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var dto = UploadDto.builder().build();

        var response = restTemplate.exchange(createUrl("123/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
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

    private void whenDefaultMoldPlasticAndColor() {
        when(moldService.getDefaultMold()).thenReturn(MOLDS.get(0));
        when(plasticService.getDefaultPlastic()).thenReturn(PLASTICS.get(0));
        when(colorService.getDefaultColor()).thenReturn(COLORS.get(0));
    }

    private void whenMoldPlasticAndColor(int moldIndex, int plasticIndex, int colorIndex) {
        when(moldService.getMold(anyLong())).thenReturn(Optional.of(MOLDS.get(moldIndex)));
        when(plasticService.getPlastic(anyLong())).thenReturn(Optional.of(PLASTICS.get(plasticIndex)));
        when(colorService.getColor(anyLong())).thenReturn(Optional.of(COLORS.get(colorIndex)));
    }

    private static UploadDto.UploadDtoBuilder uploadDto() {
        return UploadDto.builder().data("data");
    }
}
