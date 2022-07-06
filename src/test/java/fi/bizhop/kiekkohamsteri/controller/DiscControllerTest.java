package fi.bizhop.kiekkohamsteri.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.controller.provider.UUIDProvider;
import fi.bizhop.kiekkohamsteri.dto.DiscDto;
import fi.bizhop.kiekkohamsteri.dto.UploadDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.model.Ostot;
import fi.bizhop.kiekkohamsteri.projection.v1.DiscProjection;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.*;
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
    @MockBean UserService userService;
    @MockBean MoldService moldService;
    @MockBean PlasticService plasticService;
    @MockBean ColorService colorService;
    @MockBean UUIDProvider uuidProvider;

    @Captor ArgumentCaptor<Members> userCaptor;

    ObjectMapper mapper = new ObjectMapper();

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

        var response = restTemplate.postForEntity(createUrl(""), uploadDto().build(), String.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenUnableToAuthenticateUser_whenCallingUpdateImage_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var requestEntity = new HttpEntity<>(uploadDto().build());
        var response = restTemplate.exchange(createUrl("1/update-image"), PATCH, requestEntity, Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenUnableToAuthenticateUser_whenCallingUpdateDisc_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var requestEntity = new HttpEntity<>(DiscDto.builder().build());
        var response = restTemplate.exchange(createUrl("1"), PUT, requestEntity, DiscProjection.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getDiscsTest() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var response = restTemplate.getForEntity(createUrl(""), Page.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        verify(discService, times(1)).getDiscs(eq(TEST_USER), any());
    }

    @Test
    void getDiscsForSaleTest() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var response = restTemplate.getForEntity(createUrl("myytavat"), Page.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        verify(discService, times(1)).getDiscsForSale(any());
    }

    @Test
    void givenImageUploadSuccess_whenCreateDisc_thenSaveDiscAndUpdateImageReference() throws IOException {
        var user = new Members(TEST_EMAIL);
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
        verify(userService, times(1)).saveUser(user);
        verify(uploadService, times(1)).upload(dto, image);
        verify(discService, times(1)).updateImage(discId, image);

        assertEquals(SC_OK, response.getStatusCodeValue());

        assertEquals(getDiscJson("expectedNewDisc.json"), mapper.readTree(response.getBody()));
    }

    @Test
    void givenImageUploadFails_whenCreateDisc_thenDiscIsDeleted() throws IOException {
        var user = new Members(TEST_EMAIL);
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
        verify(userService, times(1)).saveUser(user);
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
    }

    @Test
    void givenValidRequest_whenUpdateImage_thenUpdateImage() throws AuthorizationException, IOException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var disc = getTestDiscFor(TEST_USER);
        var discId = 123L;
        disc.setId(discId);
        disc.setKuva("Test-123");
        var discProjection = projectionFromDisc(disc);

        when(discService.getDisc(TEST_USER, 123L)).thenReturn(discProjection);
        when(uuidProvider.getUuid()).thenReturn(TEST_UUID);

        var dto = uploadDto().build();
        var newImage = String.format("Test-123-%s", TEST_UUID);

        var response = restTemplate.exchange(createUrl("123/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

        verify(discService, times(1)).getDisc(TEST_USER, 123L);
        verify(uuidProvider, times(1)).getUuid();
        verify(uploadService, times(1)).upload(dto, newImage);
        verify(discService, times(1)).updateImage(123L, newImage);

        assertEquals(SC_NO_CONTENT, response.getStatusCodeValue());
    }

    @Test
    void givenImageUploadFails_whenUpdateImage_thenRespondInternalServerError() throws AuthorizationException, IOException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var disc = getTestDiscFor(TEST_USER);
        var discId = 123L;
        disc.setId(discId);
        disc.setKuva("Test-123");
        var discProjection = projectionFromDisc(disc);

        when(discService.getDisc(TEST_USER, 123L)).thenReturn(discProjection);
        when(uuidProvider.getUuid()).thenReturn(TEST_UUID);

        var dto = uploadDto().build();
        var newImage = String.format("Test-123-%s", TEST_UUID);
        doThrow(new IOException()).when(uploadService).upload(dto, newImage);

        var response = restTemplate.exchange(createUrl("123/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

        verify(discService, times(1)).getDisc(TEST_USER, 123L);
        verify(uuidProvider, times(1)).getUuid();
        verify(uploadService, times(1)).upload(dto, newImage);
        verify(discService, never()).updateImage(anyLong(), anyString());

        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatusCodeValue());
    }

    @Test
    void givenNotYourDisc_whenUpdateImage_thenRespondForbidden() throws AuthorizationException, IOException {
        var user = new Members(TEST_EMAIL);
        when(authService.getUser(any())).thenReturn(user);

        when(discService.getDisc(TEST_USER, 456L)).thenThrow(new AuthorizationException());

        var dto = uploadDto().build();

        var response = restTemplate.exchange(createUrl("456/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

        verify(uuidProvider, never()).getUuid();
        verify(uploadService, never()).upload(any(), anyString());
        verify(discService, never()).updateImage(anyLong(), anyString());

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenNullImageData_whenUpdateImage_thenRespondBadRequest() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var dto = UploadDto.builder().build();

        var response = restTemplate.exchange(createUrl("123/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
    }

    @Test
    void givenYourDisc_whenGetDisc_thenGetDisc() throws AuthorizationException, IOException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(discService.getDiscIfPublicOrOwn(TEST_USER, 123L)).thenReturn(projectionFromDisc(DISCS.get(0)));

        var response = restTemplate.getForEntity(createUrl("123"), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEquals(getDiscJson("expectedMyDisc.json"), mapper.readTree(response.getBody()));
    }

    @Test
    void givenNotYourDiscAndNotPublic_whenGetDisc_thenRespondForbidden() throws AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(discService.getDiscIfPublicOrOwn(TEST_USER, 456L)).thenThrow(new AuthorizationException());

        var response = restTemplate.getForEntity(createUrl("456"), String.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenValidRequest_whenUpdateDisc_thenUpdateDisc() throws IOException, AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var dto = DiscDto.builder()
                .moldId(0L)
                .muoviId(0L)
                .variId(0L)
                .build();
        whenMoldPlasticAndColor(0, 0, 0);
        when(discService.updateDisc(dto, 123L, TEST_USER, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0))).thenReturn(projectionFromDisc(DISCS.get(0)));

        var response = restTemplate.exchange(createUrl("123"), PUT, new HttpEntity<>(dto), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEquals(getDiscJson("expectedMyDisc.json"), mapper.readTree(response.getBody()));
    }

    @Test
    void givenNotYourDisc_whenUpdateDisc_thenRespondForbidden() throws AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var dto = DiscDto.builder()
                .moldId(1L)
                .muoviId(1L)
                .variId(1L)
                .build();
        whenMoldPlasticAndColor(1, 1, 1);
        when(discService.updateDisc(dto, 456L, TEST_USER, MOLDS.get(1), PLASTICS.get(1), COLORS.get(1))).thenThrow(new AuthorizationException());

        var response = restTemplate.exchange(createUrl("456"), PUT, new HttpEntity<>(dto), String.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenRuntimeException_whenUpdateDisc_thenRespond500() throws AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var dto = DiscDto.builder()
                .moldId(0L)
                .muoviId(0L)
                .variId(0L)
                .build();
        whenMoldPlasticAndColor(0, 0, 0);
        when(discService.updateDisc(dto, 123L, TEST_USER, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0))).thenThrow(new RuntimeException());

        var response = restTemplate.exchange(createUrl("123"), PUT, new HttpEntity<>(dto), String.class);

        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenValidRequest_whenDeleteDisc_thenDeleteDiscAndDecreaseDiscCountAndSaveUser() throws AuthorizationException {
        var user = new Members(TEST_EMAIL);
        user.setDiscCount(1);
        when(authService.getUser(any())).thenReturn(user);

        var response = restTemplate.exchange(createUrl("123"), DELETE, null, Object.class);

        verify(discService, times(1)).deleteDisc(123L, user);
        verify(userService, times(1)).saveUser(userCaptor.capture());

        var savedUser = userCaptor.getValue();
        assertEquals(0, savedUser.getDiscCount());

        assertEquals(SC_NO_CONTENT, response.getStatusCodeValue());
    }

    @Test
    void givenNotYourDiscOrDiscNotFound_whenDeleteDisc_thenRespondUnauthorized() throws AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        doThrow(new AuthorizationException()).when(discService).deleteDisc(123L, TEST_USER);

        var response = restTemplate.exchange(createUrl("123"), DELETE, null, Object.class);

        verify(discService, times(1)).deleteDisc(123L, TEST_USER);
        verify(userService, never()).saveUser(any());

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenValidRequest_whenBuyDisc_thenBuyDisc() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        var disc = getTestDiscFor(OTHER_USER);
        when(discService.getDiscDb(123L)).thenReturn(Optional.of(disc));

        var buys = new Ostot(disc, disc.getMember(), TEST_USER, Ostot.Status.REQUESTED);
        when(buyService.buyDisc(TEST_USER, disc)).thenReturn(buys);

        var response = restTemplate.postForEntity(createUrl("123/buy"), null, String.class);

        verify(discService, times(1)).getDiscDb(123L);
        verify(buyService, times(1)).buyDisc(TEST_USER, disc);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertNotNull(response.getBody());
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
    void givenValidRequest_whenMarkFound_thenHandleFoundDisc() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var response = restTemplate.exchange(createUrl("123/found"), PATCH, null, Object.class);

        assertEquals(SC_NO_CONTENT, response.getStatusCodeValue());
    }

    @Test
    void givenNotYourDiscOrDiscNotExisting_whenMarkFound_thenRespondForbidden() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        doThrow(new HttpResponseException(SC_FORBIDDEN, "User is not disc owner")).when(discService).handleFoundDisc(TEST_USER, 123L);

        var response = restTemplate.exchange(createUrl("123/found"), PATCH, null, Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenDiscIsNotLost_whenMarkFound_thenRespondBadRequest() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        doThrow(new HttpResponseException(SC_BAD_REQUEST, "Disc is not lost")).when(discService).handleFoundDisc(TEST_USER, 123L);

        var response = restTemplate.exchange(createUrl("123/found"), PATCH, null, Object.class);

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
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

    private JsonNode getDiscJson(String filename) throws IOException {
        var string = FileUtils.readFileToString(new File(String.format("src/test/resources/%s", filename)), UTF_8);
        return mapper.readTree(string);
    }

    private static UploadDto.UploadDtoBuilder uploadDto() {
        return UploadDto.builder().data("data");
    }
}
