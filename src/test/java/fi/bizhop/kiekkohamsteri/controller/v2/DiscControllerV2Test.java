package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscInputDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscSearchDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.DiscOutputDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
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

import java.util.NoSuchElementException;
import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.CONTROLLER;
import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
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

        var outputDto = DiscOutputDto.fromDb(DISCS.get(0));

        whenMoldPlasticAndColor(0, 0, 0);
        when(discService.updateDisc(inputDto, 123L, TEST_USER, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0))).thenReturn(outputDto);

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


    // HELPER METHODS

    private String createUrl(String endpoint) {
        return String.format("http://localhost:%d/api/v2/discs/%s", port, endpoint);
    }

    private void whenMoldPlasticAndColor(int moldIndex, int plasticIndex, int colorIndex) {
        when(moldService.getMold(anyLong())).thenReturn(Optional.of(MOLDS.get(moldIndex)));
        when(plasticService.getPlastic(anyLong())).thenReturn(Optional.of(PLASTICS.get(plasticIndex)));
        when(colorService.getColor(anyLong())).thenReturn(Optional.of(COLORS.get(colorIndex)));
    }
}
