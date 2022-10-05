package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.v2.in.CompleteGroupRequestDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.GroupCreateDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.GroupRequestDto;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.GroupRequest;
import fi.bizhop.kiekkohamsteri.model.GroupRequest.Type;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.GroupService;
import fi.bizhop.kiekkohamsteri.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.CONTROLLER;
import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static fi.bizhop.kiekkohamsteri.model.GroupRequest.Status.REQUESTED;
import static fi.bizhop.kiekkohamsteri.model.GroupRequest.Type.JOIN;
import static fi.bizhop.kiekkohamsteri.model.GroupRequest.Type.KICK;
import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GroupControllerTest extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;

    @MockBean AuthService authService;
    @MockBean UserService userService;
    @MockBean GroupService groupService;

    BaseAdder adder = new BaseAdder("group", CONTROLLER);
    BaseAdder userAdder = new BaseAdder("user/v2", CONTROLLER);

    @ParameterizedTest
    @ValueSource(strings = {"1/requests"})
    void givenUnableToAuthenticateUser_whenCallingGetEndpoints_thenRespondWithUnauthorized(String endpoint) {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(endpoint), String.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenAdminUser_whenCallingGetGroups_thenReturnGroups() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);
        when(groupService.getGroups()).thenReturn(GROUPS);

        var response = restTemplate.getForEntity(createUrl(""), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("testGroups.json"), response.getBody());
    }

    @Test
    void givenAnyUser_whenCreatingNewGroup_thenCreateNewGroupAndSaveModifiedUser() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var dto = GroupCreateDto.builder().build();
        when(groupService.createGroup(TEST_USER, dto)).thenReturn(GROUPS.get(0));

        var response = restTemplate.postForEntity(createUrl(""), dto, String.class);

        verify(userService, times(1)).saveUser(TEST_USER);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("testGroup.json"), response.getBody());
    }

    @Test
    void givenNonAdminUser_whenPostingGroupRequest_thenRegisterGroupRequest() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(userService.getUser(1L)).thenReturn(OTHER_USER);

        var dto = GroupRequestDto.builder()
                .targetUserId(1L)
                .type(JOIN)
                .info("info")
                .build();
        var request = request(dto.getType(), dto.getInfo());

        when(groupService.registerRequest(TEST_USER, OTHER_USER, 1L, dto)).thenReturn(request);

        var response = restTemplate.postForEntity(createUrl("1/requests"), dto, String.class);

        verify(groupService, never()).executeAction(any());

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("registerGroupRequest.json"), response.getBody());
    }

    @ParameterizedTest
    @MethodSource("adminUsers")
    void givenAdminOrGroupAdminUser_whenPostingGroupRequest_thenExecuteAction(User admin) throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(admin);
        when(userService.getUser(1L)).thenReturn(OTHER_USER);
        when(groupService.getGroup(1L)).thenReturn(Optional.of(GROUPS.get(0)));
        when(groupService.executeAction(any(GroupRequest.class))).thenReturn(OTHER_USER);

        var dto = GroupRequestDto.builder()
                .targetUserId(1L)
                .type(KICK)
                .info("kicking user from group")
                .build();

        var response = restTemplate.postForEntity(createUrl("1/requests"), dto, String.class);

        verify(groupService, never()).registerRequest(any(), any(), anyLong(), any());
        verify(userService, times(1)).saveUser(OTHER_USER);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("executedAction.json"), response.getBody());
    }

    @Test
    void givenInvalidDto_whenPostingGroupRequest_thenException() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var dto = GroupRequestDto.builder().build();

        var response = restTemplate.postForEntity(createUrl("1/requests"), dto, String.class);

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
    }

    @Test
    void givenTargetUserNotFound_whenPostingGroupRequest_thenException() {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(userService.getUser(1L)).thenThrow(new NoSuchElementException());

        var dto = GroupRequestDto.builder()
                .targetUserId(1L)
                .build();

        var response = restTemplate.postForEntity(createUrl("1/requests"), dto, String.class);

        assertEquals(SC_NOT_FOUND, response.getStatusCodeValue());
        assertEqualsJson(adder.create("userNotFound.json"), response.getBody());
    }

    @ParameterizedTest
    @MethodSource("adminUsers")
    void givenGroupNotFound_whenAdminUserPostingGroupRequest_thenException(User admin) {
        when(authService.getUser(any())).thenReturn(admin);
        when(userService.getUser(1L)).thenReturn(OTHER_USER);
        when(groupService.getGroup(1L)).thenReturn(Optional.empty());

        var dto = GroupRequestDto.builder()
                .targetUserId(1L)
                .build();

        var response = restTemplate.postForEntity(createUrl("1/requests"), dto, String.class);

        assertEquals(SC_NOT_FOUND, response.getStatusCodeValue());
        assertEqualsJson(adder.create("groupNotFound.json"), response.getBody());
    }

    @Test
    void givenGroupNotFound_whenRegisteringRequest_thenException() throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(TEST_USER);
        when(userService.getUser(1L)).thenReturn(OTHER_USER);

        var dto = GroupRequestDto.builder()
                .targetUserId(1L)
                .build();
        when(groupService.registerRequest(TEST_USER, OTHER_USER, 1L, dto)).thenThrow(new HttpResponseException(SC_NOT_FOUND, "Group not found"));

        var response = restTemplate.postForEntity(createUrl("1/requests"), dto, String.class);

        assertEquals(SC_NOT_FOUND, response.getStatusCodeValue());
        assertEqualsJson(adder.create("groupNotFound.json"), response.getBody());
    }

    @Test
    void givenAdminUser_whenGettingAllRequests_thenReturnAllRequests() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var requests = List.of(request());
        when(groupService.getGroupRequests()).thenReturn(requests);

        var response = restTemplate.getForEntity(createUrl("requests"), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("requests.json"), response.getBody());
    }

    @Test
    void givenAdminUser_whenGettingGroupRequests_thenReturnGroupRequests() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var requests = List.of(request());
        when(groupService.getGroupRequests()).thenReturn(requests);

        var response = restTemplate.getForEntity(createUrl("requests"), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("requests.json"), response.getBody());
    }

    @Test
    void givenGroupAdminUser_whenGettingGroupRequests_thenReturnGroupRequests() {
        when(authService.getUser(any())).thenReturn(GROUP_ADMIN_USER);

        var requests = List.of(request());
        when(groupService.getGroupRequests(Set.of(1L))).thenReturn(requests);

        var response = restTemplate.getForEntity(createUrl("requests"), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("requests.json"), response.getBody());
    }

    @ParameterizedTest
    @MethodSource("adminUsers")
    void givenAdminUser_whenCompletingRequest_thenCompleteRequest(User admin) throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(admin);

        var request = request();
        var dto = CompleteGroupRequestDto.builder()
                .confirm(true)
                .build();

        when(groupService.getGroupRequest(1L)).thenReturn(Optional.of(request));
        when(groupService.completeRequest(request, true)).thenReturn(OTHER_USER);

        var response = restTemplate.postForEntity(createUrl("1/requests/1"), dto, String.class);

        verify(userService, times(1)).saveUser(OTHER_USER);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(userAdder.create("otherUser.json"), response.getBody());
    }

    @ParameterizedTest
    @MethodSource("adminUsers")
    void givenRequestNotFound_whenCompletingRequest_thenException(User admin) {
        when(authService.getUser(any())).thenReturn(admin);

        var dto = CompleteGroupRequestDto.builder()
                .confirm(true)
                .build();

        when(groupService.getGroupRequest(1L)).thenReturn(Optional.empty());

        var response = restTemplate.postForEntity(createUrl("1/requests/1"), dto, String.class);

        verify(userService, never()).saveUser(any(User.class));

        assertEquals(SC_NOT_FOUND, response.getStatusCodeValue());
        assertEqualsJson(adder.create("requestNotFound.json"), response.getBody());
    }

    @ParameterizedTest
    @MethodSource("adminUsers")
    void givenRequestAlreadyCompleted_whenCompletingRequest_thenException(User admin) throws HttpResponseException {
        when(authService.getUser(any())).thenReturn(admin);

        var request = request();
        var dto = CompleteGroupRequestDto.builder()
                .confirm(true)
                .build();

        when(groupService.getGroupRequest(1L)).thenReturn(Optional.of(request));
        when(groupService.completeRequest(request, true)).thenThrow(new HttpResponseException(SC_BAD_REQUEST, "Request already completed"));

        var response = restTemplate.postForEntity(createUrl("1/requests/1"), dto, String.class);

        verify(userService, never()).saveUser(any(User.class));

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertEqualsJson(adder.create("requestAlreadyCompleted.json"), response.getBody());
    }

    // HELPER METHODS

    private String createUrl(String endpoint) {
        return String.format("http://localhost:%d/api/v2/groups/%s", port, endpoint);
    }

    private GroupRequest request() {
        return request(JOIN, "info");
    }

    private GroupRequest request(Type type, String info) {
        return new GroupRequest(GROUPS.get(0), TEST_USER, OTHER_USER, type, REQUESTED, info );
    }

    private static Stream<Arguments> adminUsers() {
        return Stream.of(
                Arguments.of(ADMIN_USER),
                Arguments.of(GROUP_ADMIN_USER)
        );
    }
}
