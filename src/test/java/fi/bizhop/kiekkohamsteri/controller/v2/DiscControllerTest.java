package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.v2.in.*;
import fi.bizhop.kiekkohamsteri.dto.v2.out.DiscOutputDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.*;
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
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DiscControllerTest extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;

    @MockBean AuthService authService;
    @MockBean UserService userService;
    @MockBean ManufacturerService manufacturerService;
    @MockBean DiscService discService;
    @MockBean MoldService moldService;
    @MockBean PlasticService plasticService;
    @MockBean ColorService colorService;
    @MockBean UploadService uploadService;
    @MockBean BuyService buyService;
    @MockBean Clock clock;

    BaseAdder adder = new BaseAdder("disc/v2", CONTROLLER);


    //AUTHENTICATION

    @ParameterizedTest
    @ValueSource(strings = {"", "for-sale", "1", "lost"})
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
    void givenUnableToAuthenticateUser_whenCallingCreateDisc_thenRespondWithUnauthorized() {
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
    void givenUnableToAuthenticateUser_whenCallingGetMolds_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl("molds"), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenNonAdminUser_whenCallingGetMolds_thenRespondForbidden() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var response = restTemplate.getForEntity(createUrl("molds"), Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenUnableToAuthenticateUser_whenCreatingMold_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.postForEntity(createUrl("molds"), MoldCreateDto.builder().build(), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenNonAdminUser_whenCreatingMold_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var response = restTemplate.postForEntity(createUrl("molds"), MoldCreateDto.builder().build(), Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenUnableToAuthenticateUser_whenCallingGetPlastics_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl("plastics"), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenNonAdminUser_whenCallingGetPlastics_thenRespondForbidden() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var response = restTemplate.getForEntity(createUrl("plastics"), Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenUnableToAuthenticateUser_whenCreatingPlastic_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.postForEntity(createUrl("plastics"), PlasticCreateDto.builder().build(), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenNonAdminUser_whenCreatingPlastic_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var response = restTemplate.postForEntity(createUrl("plastics"), PlasticCreateDto.builder().build(), Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }


    //DISCS

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
        when(discService.updateDisc(inputDto, TEST_UUID, TEST_USER, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0))).thenReturn(DISCS.get(0));

        var response = restTemplate.exchange(createUrl(TEST_UUID), PUT, new HttpEntity<>(inputDto), String.class);

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
        when(discService.updateDisc(inputDto, TEST_UUID, TEST_USER, MOLDS.get(1), PLASTICS.get(1), COLORS.get(1))).thenThrow(new AuthorizationException());

        var response = restTemplate.exchange(createUrl(TEST_UUID), PUT, new HttpEntity<>(inputDto), String.class);

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
        when(discService.updateDisc(inputDto, TEST_UUID, TEST_USER, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0))).thenThrow(new RuntimeException());

        var response = restTemplate.exchange(createUrl(TEST_UUID), PUT, new HttpEntity<>(inputDto), String.class);

        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenYourDisc_whenGetDisc_thenGetDisc() throws AuthorizationException, HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(discService.getDiscIfPublicOrOwnV2(TEST_USER, TEST_UUID)).thenReturn(DISCS.get(0));

        var response = restTemplate.getForEntity(createUrl(TEST_UUID), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("myDisc.json"), response.getBody());
    }

    @Test
    void givenNotYourDiscAndNotPublic_whenGetDisc_thenRespondForbidden() throws AuthorizationException, HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(discService.getDiscIfPublicOrOwnV2(TEST_USER, TEST_UUID)).thenThrow(new AuthorizationException());

        var response = restTemplate.getForEntity(createUrl(TEST_UUID), String.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenDiscNotFound_whenGetDisc_thenRespondNotFound() throws AuthorizationException, HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(discService.getDiscIfPublicOrOwnV2(TEST_USER, TEST_UUID)).thenThrow(new HttpResponseException(SC_NOT_FOUND, "Disc not found"));

        var response = restTemplate.getForEntity(createUrl(TEST_UUID), String.class);

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
    void givenValidUser_whenCreateDisc_thenSaveDiscWithNoImage() {
        var user = new User(TEST_EMAIL);
        when(authService.getUser(any())).thenReturn(user);
        whenDefaultMoldPlasticAndColor();

        var disc = getTestDiscFor(user);
        var discId = 123L;
        disc.setId(discId);

        when(discService.newDisc(user, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0))).thenReturn(disc);
        when(discService.saveDisc(any(Disc.class))).then(returnsFirstArg());

        var dto = uploadDto().build();

        var response = restTemplate.postForEntity(createUrl(""), dto, String.class);

        verify(discService, times(1)).newDisc(user, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0));
        verify(discService, times(1)).saveDisc(any(Disc.class));

        assertEquals(SC_OK, response.getStatusCodeValue());

        assertEqualsJson(adder.create("newDisc.json"), response.getBody());
    }

    @Test
    void givenValidRequest_whenUpdateImage_thenUpdateImage() throws AuthorizationException, IOException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var disc = getTestDiscFor(TEST_USER);
        var discId = 123L;
        disc.setId(discId);

        when(discService.getDisc(TEST_USER, TEST_UUID)).thenReturn(disc);
        when(clock.instant()).thenReturn(TEST_TIMESTAMP);
        when(discService.saveDisc(any(Disc.class))).then(returnsFirstArg());

        var dto = uploadDto().build();
        var newImage = String.format("%s-%d-%d", TEST_USER.getUsername(), discId, TEST_TIMESTAMP.toEpochMilli());

        var response = restTemplate.exchange(createUrl(TEST_UUID + "/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

        verify(discService, times(1)).getDisc(TEST_USER, TEST_UUID);
        verify(clock, times(1)).instant();
        verify(uploadService, times(1)).upload(dto, newImage);
        verify(discService, times(1)).saveDisc(any(Disc.class));

        assertEquals(SC_OK, response.getStatusCodeValue());

        System.out.println(response.getBody());
        assertEqualsJson(adder.create("imageUpdatedDisc.json"), response.getBody());
    }

    @Test
    void givenImageUploadFails_whenUpdateImage_thenRespondInternalServerError() throws AuthorizationException, IOException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var disc = getTestDiscFor(TEST_USER);
        var discId = 123L;
        disc.setId(discId);

        when(discService.getDisc(TEST_USER, TEST_UUID)).thenReturn(disc);
        when(clock.instant()).thenReturn(TEST_TIMESTAMP);

        var dto = uploadDto().build();
        var newImage = String.format("%s-%d-%d", TEST_USER.getUsername(), discId, TEST_TIMESTAMP.toEpochMilli());
        doThrow(new IOException()).when(uploadService).upload(dto, newImage);

        var response = restTemplate.exchange(createUrl(TEST_UUID + "/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

        verify(discService, times(1)).getDisc(TEST_USER, TEST_UUID);
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

        when(discService.getDisc(TEST_USER, TEST_UUID)).thenThrow(new AuthorizationException());

        var dto = uploadDto().build();

        var response = restTemplate.exchange(createUrl(TEST_UUID + "/update-image"), PATCH, new HttpEntity<>(dto), Object.class);

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

    @Test
    void givenValidRequest_whenDeleteDisc_thenDeleteDisc() throws AuthorizationException {
        var user = new User(TEST_EMAIL);
        when(authService.getUser(any())).thenReturn(user);

        var response = restTemplate.exchange(createUrl(TEST_UUID), DELETE, null, Object.class);

        verify(discService, times(1)).deleteDisc(TEST_UUID, user);

        assertEquals(SC_NO_CONTENT, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenNotYourDiscOrDiscNotFound_whenDeleteDisc_thenRespondUnauthorized() throws AuthorizationException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        doThrow(new AuthorizationException()).when(discService).deleteDisc(TEST_UUID, TEST_USER);

        var response = restTemplate.exchange(createUrl(TEST_UUID), DELETE, null, Object.class);

        verify(discService, times(1)).deleteDisc(TEST_UUID, TEST_USER);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenValidRequest_whenBuyDisc_thenBuyDisc() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        var disc = getTestDiscFor(OTHER_USER);
        when(discService.getDisc(TEST_UUID)).thenReturn(disc);

        var buys = new Buy(disc, disc.getOwner(), TEST_USER, Buy.Status.REQUESTED);
        when(buyService.buyDisc(TEST_USER, disc)).thenReturn(buys);

        var response = restTemplate.postForEntity(createUrl(TEST_UUID + "/buy"), null, String.class);

        verify(discService, times(1)).getDisc(TEST_UUID);
        verify(buyService, times(1)).buyDisc(TEST_USER, disc);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("buyDiscRequest.json"), response.getBody());
    }

    @Test
    void givenDiscNotFoundOrNotForSale_whenBuyDisc_thenRespondForbidden() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(discService.getDisc(TEST_UUID)).thenReturn(null);
        doThrow(new HttpResponseException(SC_FORBIDDEN, "Not for sale")).when(buyService).buyDisc(TEST_USER, null);

        var response = restTemplate.postForEntity(createUrl(TEST_UUID + "/buy"), null, String.class);

        verify(discService, times(1)).getDisc(TEST_UUID);
        verify(buyService, times(1)).buyDisc(TEST_USER, null);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenAlreadyBuyingDisc_whenBuyDisc_thenRespondBadRequest() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        var disc = getTestDiscFor(OTHER_USER);
        when(discService.getDisc(TEST_UUID)).thenReturn(disc);
        when(buyService.buyDisc(TEST_USER, disc)).thenThrow(new HttpResponseException(SC_BAD_REQUEST, "You are already buying this disc"));

        var response = restTemplate.postForEntity(createUrl(TEST_UUID + "/buy"), null, String.class);

        verify(discService, times(1)).getDisc(TEST_UUID);
        verify(buyService, times(1)).buyDisc(TEST_USER, disc);

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenYourOwnDisc_whenBuyDisc_thenRespondBadRequest() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        var disc = getTestDiscFor(TEST_USER);
        when(discService.getDisc(TEST_UUID)).thenReturn(disc);
        when(buyService.buyDisc(TEST_USER, disc)).thenThrow(new HttpResponseException(SC_BAD_REQUEST, "You can't buy your own disc"));

        var response = restTemplate.postForEntity(createUrl(TEST_UUID + "/buy"), null, String.class);

        verify(discService, times(1)).getDisc(TEST_UUID);
        verify(buyService, times(1)).buyDisc(TEST_USER, disc);

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenValidRequest_whenMarkFound_thenHandleFoundDisc() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var response = restTemplate.exchange(createUrl(TEST_UUID + "/found"), PATCH, null, Object.class);

        verify(discService, times(1)).handleFoundDisc(TEST_USER, TEST_UUID);

        assertEquals(SC_NO_CONTENT, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenNotYourDiscOrDiscNotExisting_whenMarkFound_thenRespondForbidden() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        doThrow(new HttpResponseException(SC_FORBIDDEN, "User is not disc owner")).when(discService).handleFoundDisc(TEST_USER, TEST_UUID);

        var response = restTemplate.exchange(createUrl(TEST_UUID + "/found"), PATCH, null, Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenDiscIsNotLost_whenMarkFound_thenRespondBadRequest() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        doThrow(new HttpResponseException(SC_BAD_REQUEST, "Disc is not lost")).when(discService).handleFoundDisc(TEST_USER, TEST_UUID);

        var response = restTemplate.exchange(createUrl(TEST_UUID + "/found"), PATCH, null, Object.class);

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }


    //PLASTICS

    @Test
    void givenValidRequestWithoutManufacturerId_whenGetPlastics_thenReturnAllPlastics() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var page = new PageImpl<>(PLASTICS);
        when(plasticService.getPlastics(any())).thenReturn(page);

        var response = restTemplate.getForEntity(createUrl("plastics"), String.class);

        verify(plasticService, times(1)).getPlastics(any());
        verify(manufacturerService, never()).getManufacturer(anyLong());
        verify(plasticService, never()).getPlasticsByManufacturer(any(), any());

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("plastics/all.json"), response.getBody());
    }

    @Test
    void givenValidRequestWithManufacturerId_whenGetPlastics_thenReturnPlasticsByManufacturer() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var manufacturer = MANUFACTURERS.get(0);
        var page = new PageImpl<>(getPlastics(manufacturer));
        when(manufacturerService.getManufacturer(0L)).thenReturn(Optional.of(manufacturer));
        when(plasticService.getPlasticsByManufacturer(eq(manufacturer), any())).thenReturn(page);

        var response = restTemplate.getForEntity(createUrl("plastics?manufacturerId=0"), String.class);

        verify(plasticService, never()).getPlastics(any());
        verify(manufacturerService, times(1)).getManufacturer(0L);
        verify(plasticService, times(1)).getPlasticsByManufacturer(eq(manufacturer), any());

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("plastics/discmania.json"), response.getBody());
    }

    @Test
    void givenManufacturerNotFound_whenGetPlastics_thenRespondBadRequest() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        when(manufacturerService.getManufacturer(66L)).thenReturn(Optional.empty());

        var response = restTemplate.getForEntity(createUrl("plastics?manufacturerId=66"), String.class);

        verify(plasticService, never()).getPlastics(any());
        verify(manufacturerService, times(1)).getManufacturer(66L);
        verify(plasticService, never()).getPlasticsByManufacturer(any(), any());

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenValidRequest_whenCreatePlastic_thenCreatePlastic() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var manufacturer = MANUFACTURERS.get(0);
        var dto = PlasticCreateDto.builder().manufacturerId(0L).build();
        var plastic = getTestPlastic(manufacturer);
        when(manufacturerService.getManufacturer(0L)).thenReturn(Optional.of(manufacturer));
        when(plasticService.createPlastic(dto, manufacturer)).thenReturn(plastic);

        var response = restTemplate.postForEntity(createUrl("plastics"), dto, String.class);

        verify(manufacturerService, times(1)).getManufacturer(0L);
        verify(plasticService, times(1)).createPlastic(dto, manufacturer);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("plastics/create.json"), response.getBody());
    }

    @Test
    void givenManufacturerNotFound_whenCreatePlastic_thenRespondBadRequest() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var dto = PlasticCreateDto.builder().manufacturerId(0L).build();
        when(manufacturerService.getManufacturer(0L)).thenReturn(Optional.empty());

        var response = restTemplate.postForEntity(createUrl("plastics"), dto, String.class);

        verify(manufacturerService, times(1)).getManufacturer(0L);
        verify(plasticService, never()).createPlastic(any(), any());

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }


    //MOLDS

    @Test
    void givenValidRequestWithoutManufacturerId_whenGetMolds_thenReturnAllMolds() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var page = new PageImpl<>(MOLDS);
        when(moldService.getMolds(any())).thenReturn(page);

        var response = restTemplate.getForEntity(createUrl("molds"), String.class);

        verify(moldService, times(1)).getMolds(any());
        verify(manufacturerService, never()).getManufacturer(anyLong());
        verify(moldService, never()).getMoldsByManufacturer(any(), any());

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("molds/all.json"), response.getBody());
    }

    @Test
    void givenValidRequestWithManufacturerId_whenGetMolds_thenReturnMoldsByManufacturer() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var manufacturer = MANUFACTURERS.get(0);
        var page = new PageImpl<>(getMolds(manufacturer));
        when(manufacturerService.getManufacturer(0L)).thenReturn(Optional.of(manufacturer));
        when(moldService.getMoldsByManufacturer(eq(manufacturer), any())).thenReturn(page);

        var response = restTemplate.getForEntity(createUrl("molds?manufacturerId=0"), String.class);

        verify(moldService, never()).getMolds(any());
        verify(manufacturerService, times(1)).getManufacturer(0L);
        verify(moldService, times(1)).getMoldsByManufacturer(eq(manufacturer), any());

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("molds/discmania.json"), response.getBody());
    }

    @Test
    void givenManufacturerNotFound_whenGetMolds_thenRespondBadRequest() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        when(manufacturerService.getManufacturer(66L)).thenReturn(Optional.empty());

        var response = restTemplate.getForEntity(createUrl("molds?manufacturerId=66"), String.class);

        verify(moldService, never()).getMolds(any());
        verify(manufacturerService, times(1)).getManufacturer(66L);
        verify(moldService, never()).getMoldsByManufacturer(any(), any());

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenValidRequest_whenCreateMold_thenCreateMold() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var manufacturer = MANUFACTURERS.get(0);
        var dto = MoldCreateDto.builder().manufacturerId(0L).build();
        var mold = getTestMold(manufacturer);
        when(manufacturerService.getManufacturer(0L)).thenReturn(Optional.of(manufacturer));
        when(moldService.createMold(dto, manufacturer)).thenReturn(mold);

        var response = restTemplate.postForEntity(createUrl("molds"), dto, String.class);

        verify(manufacturerService, times(1)).getManufacturer(0L);
        verify(moldService, times(1)).createMold(dto, manufacturer);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("molds/create.json"), response.getBody());
    }

    @Test
    void givenManufacturerNotFound_whenCreateMold_thenRespondBadRequest() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var dto = MoldCreateDto.builder().manufacturerId(0L).build();
        when(manufacturerService.getManufacturer(0L)).thenReturn(Optional.empty());

        var response = restTemplate.postForEntity(createUrl("molds"), dto, String.class);

        verify(manufacturerService, times(1)).getManufacturer(0L);
        verify(moldService, never()).createMold(any(MoldCreateDto.class), any());

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }


    //HELPER METHODS

    private String createUrl(String endpoint) {
        return String.format("http://localhost:%d/api/v2/discs/%s", port, endpoint);
    }

    private void whenDefaultMoldPlasticAndColor() {
        when(moldService.getDefaultMold()).thenReturn(MOLDS.get(0));
        when(plasticService.getDefaultPlastic()).thenReturn(PLASTICS.get(0));
        when(colorService.getDefaultColor()).thenReturn(COLORS.get(0));
    }

    private void whenMoldPlasticAndColor(int moldIndex, int plasticIndex, int colorIndex) {
        when(moldService.getMold(anyLong())).thenReturn(MOLDS.get(moldIndex));
        when(plasticService.getPlastic(anyLong())).thenReturn(PLASTICS.get(plasticIndex));
        when(colorService.getColor(anyLong())).thenReturn(COLORS.get(colorIndex));
    }

    private static UploadDto.UploadDtoBuilder uploadDto() {
        return UploadDto.builder().data("data");
    }

    private Plastic getTestPlastic(Manufacturer manufacturer) {
        var plastic = new Plastic();
        plastic.setId(66L);
        plastic.setManufacturer(manufacturer);
        plastic.setName("New Plastic");
        return plastic;
    }

    private Mold getTestMold(Manufacturer manufacturer) {
        var mold = new Mold();
        mold.setId(66L);
        mold.setName("New Mold");
        mold.setManufacturer(manufacturer);
        mold.setSpeed(1.0);
        mold.setGlide(2.0);
        mold.setStability(3.0);
        mold.setFade(4.0);
        return mold;
    }
}
