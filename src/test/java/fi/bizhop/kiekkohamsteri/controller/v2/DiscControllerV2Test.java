package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscInputDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscSearchDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.UploadDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.Disc;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.Clock;
import java.util.NoSuchElementException;
import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.CONTROLLER;
import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DiscControllerV2Test extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;

    @MockBean AuthService authService;
    @MockBean UserService userService;
    @MockBean DiscService discService;
    @MockBean MoldService moldService;
    @MockBean PlasticService plasticService;
    @MockBean ColorService colorService;
    @MockBean UploadService uploadService;
    @MockBean Clock clock;

    BaseAdder adder = new BaseAdder("disc/v2", CONTROLLER);

    @ParameterizedTest
    @ValueSource(strings = {""})
    void givenUnableToAuthenticateUser_whenCallingGetEndpoints_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(endpoint), String.class);

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
    void getDiscsTest() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var discs = getDiscsByUserV2(TEST_USER);
        when(discService.getDiscsV2(eq(TEST_USER), any())).thenReturn(new PageImpl<>(discs));

        var response = restTemplate.getForEntity(createUrl(""), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("getDiscs.json"), response.getBody());
    }

    @Test
    void givenUserNotFound_whenGetOtherUserDiscs_thenBadRequest() {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(userService.getUser(1L)).thenThrow(new NoSuchElementException());

        var response = restTemplate.getForEntity(createUrl("?userId=1"), String.class);

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
    }

    @Test
    void givenNoCommonGroup_whenGetOtherUserDiscs_thenForbidden() {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(userService.getUser(1L)).thenReturn(OTHER_USER);

        var response = restTemplate.getForEntity(createUrl("?userId=1"), String.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenCommonGroup_whenGetOtherUserDiscs_thenReturnDiscs() {
        when(authService.getUser(any())).thenReturn(GROUP_ADMIN_USER);
        when(userService.getUser(1L)).thenReturn(TEST_USER);

        var discs = getDiscsByUserV2(TEST_USER);
        when(discService.getDiscsV2(eq(TEST_USER), any())).thenReturn(new PageImpl<>(discs));

        var response = restTemplate.getForEntity(createUrl("?userId=1"), String.class);

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

    @Test
    void givenValidRequest_whenUpdateDisc_thenUpdateDisc() throws AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var inputDto = DiscInputDto.builder()
                .moldId(0L)
                .plasticId(0L)
                .colorId(0L)
                .build();

        whenMoldPlasticAndColor(0, 0, 0);
        when(discService.updateDisc(inputDto, 123L, TEST_USER, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0))).thenReturn(DISCS.get(0));

        var response = restTemplate.exchange(createUrl("123"), PUT, new HttpEntity<>(inputDto), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("myDisc.json"), response.getBody());
    }

    @Test
    void givenNotYourDisc_whenUpdateDisc_thenRespondForbidden() throws AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var inputDto = DiscInputDto.builder()
                .moldId(1L)
                .plasticId(1L)
                .colorId(1L)
                .build();
        whenMoldPlasticAndColor(1, 1, 1);
        when(discService.updateDisc(inputDto, 456L, TEST_USER, MOLDS.get(1), PLASTICS.get(1), COLORS.get(1))).thenThrow(new AuthorizationException());

        var response = restTemplate.exchange(createUrl("456"), PUT, new HttpEntity<>(inputDto), String.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenRuntimeException_whenUpdateDisc_thenRespond500() throws AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var inputDto = DiscInputDto.builder()
                .moldId(0L)
                .plasticId(0L)
                .colorId(0L)
                .build();
        whenMoldPlasticAndColor(0, 0, 0);
        when(discService.updateDisc(inputDto, 123L, TEST_USER, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0))).thenThrow(new RuntimeException());

        var response = restTemplate.exchange(createUrl("123"), PUT, new HttpEntity<>(inputDto), String.class);

        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenYourDisc_whenGetDisc_thenGetDisc() throws AuthorizationException, HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(discService.getDiscIfPublicOrOwnV2(TEST_USER, 123L)).thenReturn(DISCS.get(0));

        var response = restTemplate.getForEntity(createUrl("123"), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("myDisc.json"), response.getBody());
    }

    @Test
    void givenNotYourDiscAndNotPublic_whenGetDisc_thenRespondForbidden() throws AuthorizationException, HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(discService.getDiscIfPublicOrOwnV2(TEST_USER, 456L)).thenThrow(new AuthorizationException());

        var response = restTemplate.getForEntity(createUrl("456"), String.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenDiscNotFound_whenGetDisc_thenRespondNotFound() throws AuthorizationException, HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(discService.getDiscIfPublicOrOwnV2(TEST_USER, 789L)).thenThrow(new HttpResponseException(SC_NOT_FOUND, "Disc not found"));

        var response = restTemplate.getForEntity(createUrl("789"), String.class);

        assertEquals(SC_NOT_FOUND, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getDiscsForSaleTest() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var page = new PageImpl<>(getDiscsByUserV2(OTHER_USER));
        when(discService.getDiscsForSaleV2(any())).thenReturn(page);

        var response = restTemplate.getForEntity(createUrl("for-sale"), String.class);

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

        var image = String.format("%s-%d", user.getUsername(), discId);

        when(discService.newDisc(user, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0))).thenReturn(disc);
        when(discService.saveDisc(any(Disc.class))).thenAnswer(i -> i.getArgument(0));

        var dto = uploadDto().build();

        var response = restTemplate.postForEntity(createUrl(""), dto, String.class);

        verify(discService, times(1)).newDisc(user, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0));
        verify(uploadService, times(1)).upload(dto, image);
        verify(discService, times(1)).saveDisc(any(Disc.class));

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

        var image = String.format("%s-%d", user.getUsername(), discId);

        when(discService.newDisc(user, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0))).thenReturn(disc);

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

        when(discService.getDisc(TEST_USER, 123L)).thenReturn(disc);
        when(clock.instant()).thenReturn(TEST_TIMESTAMP);

        var dto = uploadDto().build();
        var newImage = String.format("Test-123-%d", TEST_TIMESTAMP.toEpochMilli());

        var response = restTemplate.exchange(createUrl("123/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

        verify(discService, times(1)).getDisc(TEST_USER, 123L);
        verify(clock, times(1)).instant();
        verify(uploadService, times(1)).upload(dto, newImage);
        verify(discService, times(1)).saveDisc(any(Disc.class));

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

        when(discService.getDisc(TEST_USER, 123L)).thenReturn(disc);
        when(clock.instant()).thenReturn(TEST_TIMESTAMP);

        var dto = uploadDto().build();
        var newImage = String.format("Test-123-%d", TEST_TIMESTAMP.toEpochMilli());
        doThrow(new IOException()).when(uploadService).upload(dto, newImage);

        var response = restTemplate.exchange(createUrl("123/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

        verify(discService, times(1)).getDisc(TEST_USER, 123L);
        verify(clock, times(1)).instant();
        verify(uploadService, times(1)).upload(dto, newImage);
        verify(discService, never()).saveDisc(any());

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
        verify(discService, never()).saveDisc(any());

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

    // HELPER METHODS

    private String createUrl(String endpoint) {
        return String.format("http://localhost:%d/api/v2/discs/%s", port, endpoint);
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
