package fi.bizhop.kiekkohamsteri.controller.v1;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.v1.in.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.User;
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
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.CONTROLLER;
import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.PATCH;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;

    @MockBean AuthService authService;
    @MockBean UserService userService;
    @MockBean DiscService discService;

    BaseAdder adder = new BaseAdder("user", CONTROLLER);

    @ParameterizedTest
    @ValueSource(strings = {"", "1", "leaders", "me"})
    void givenUnableToAuthenticateUser_whenCallingGetEndpoints_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(endpoint), String.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1"})
    void givenNonAdminUser_whenCallingRestrictedGetEndpoints_thenRespondWithForbidden(String endpoint) {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(userService.getUser(1L)).thenReturn(OTHER_USER);

        var response = restTemplate.getForEntity(createUrl(endpoint), String.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenAdminUser_whenGetUsers_thenGetUsers() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);
        when(userService.getUsers()).thenReturn(USERS);

        var response = restTemplate.getForEntity(createUrl(""), String.class);

        //verify disc counts updated
        verify(discService, times(1)).updateDiscCounts(USERS);
        verify(userService, times(1)).saveUsers(USERS);

        verify(userService, times(1)).getUsers();

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("testUsers.json"), response.getBody());
    }

    @Test
    void givenAdminUser_whenGetDetailsForOtherUser_thenGetDetails() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);
        when(userService.getUser(1L)).thenReturn(TEST_USER);

        var response = restTemplate.getForEntity(createUrl("1"), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("testUser.json"), response.getBody());
    }

    @Test
    void givenUser_whenGetDetailsForSelf_thenGetDetails() {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(userService.getUser(1L)).thenReturn(TEST_USER);

        var response = restTemplate.getForEntity(createUrl("1"), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("testUser.json"), response.getBody());
    }

    @Test
    void givenUser_whenUpdateOwnDetails_thenUpdateDetails() {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(userService.getUser(1L)).thenReturn(TEST_USER);

        var dto = UserUpdateDto.builder().build();
        when(userService.updateDetails(TEST_USER, dto, false)).thenReturn(TEST_USER);

        var response = restTemplate.exchange(createUrl("1"), PATCH, new HttpEntity<>(dto), String.class);

        //non admin user should invoke non admin request
        verify(userService, times(1)).updateDetails(TEST_USER, dto, false);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("testUser.json"), response.getBody());
    }

    @Test
    void givenUnableToAuthenticateUser_whenUpdateDetails_thenUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var dto = UserUpdateDto.builder().build();
        var response = restTemplate.exchange(createUrl("1"), PATCH, new HttpEntity<>(dto), String.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenNonAdminUser_whenUpdateOtherUserDetails_thenForbidden() {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(userService.getUser(1L)).thenReturn(OTHER_USER);

        var dto = UserUpdateDto.builder().build();
        var response = restTemplate.exchange(createUrl("1"), PATCH, new HttpEntity<>(dto), String.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenAdminUser_whenUpdateOtherUserDetails_thenUpdateDetails() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);
        when(userService.getUser(1L)).thenReturn(TEST_USER);

        var dto = UserUpdateDto.builder().build();
        when(userService.updateDetails(TEST_USER, dto, true)).thenReturn(TEST_USER);

        var response = restTemplate.exchange(createUrl("1"), PATCH, new HttpEntity<>(dto), String.class);

        //admin user should invoke admin request
        verify(userService, times(1)).updateDetails(TEST_USER, dto, true);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("testUser.json"), response.getBody());
    }

    @Test
    void givenUnableToAuthenticateUser_whenSetUserLevel_thenUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        //called endpoint is deprecated
        var response = restTemplate.exchange(createUrl("1/level/2"), PATCH, null, String.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenNonAdminUser_whenSetUserLevel_thenForbidden() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        //called endpoint is deprecated
        var response = restTemplate.exchange(createUrl("1/level/2"), PATCH, null, String.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenAdminUser_whenSetUserLevel_thenSetLevel() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var userWithLevel2 = new User(TEST_EMAIL);
        userWithLevel2.setLevel(2);

        var dto = UserUpdateDto.builder().level(2).build();

        when(userService.getUser(1L)).thenReturn(TEST_USER);
        when(userService.updateDetails(TEST_USER, dto, true)).thenReturn(userWithLevel2);

        //called endpoint is deprecated
        var response = restTemplate.exchange(createUrl("1/level/2"), PATCH, null, String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());

        assertEqualsJson(adder.create("userLevel2.json"), response.getBody());
    }

    @Test
    void givenPublicListTrue_whenUpdateDetails_thenMakeDiscsPublic() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var dto = UserUpdateDto.builder().publicList(true).build();

        when(userService.getUser(1L)).thenReturn(TEST_USER);

        var response = restTemplate.exchange(createUrl("1"), PATCH, new HttpEntity<>(dto), String.class);

        verify(discService, times(1)).makeDiscsPublic(TEST_USER);
        assertEquals(SC_OK, response.getStatusCodeValue());
    }


    // HELPER METHODS

    private String createUrl(String endpoint) {
        return String.format("http://localhost:%d/api/user/%s", port, endpoint);
    }
}
