package fi.bizhop.kiekkohamsteri.service;


import fi.bizhop.kiekkohamsteri.db.GroupRepository;
import fi.bizhop.kiekkohamsteri.db.GroupRequestRepository;
import fi.bizhop.kiekkohamsteri.db.RoleRepository;
import fi.bizhop.kiekkohamsteri.dto.v2.in.GroupCreateDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.GroupRequestDto;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.Group;
import fi.bizhop.kiekkohamsteri.model.GroupRequest;
import fi.bizhop.kiekkohamsteri.model.Role;
import fi.bizhop.kiekkohamsteri.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.TestObjects.OTHER_EMAIL;
import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_EMAIL;
import static fi.bizhop.kiekkohamsteri.model.GroupRequest.Status.*;
import static fi.bizhop.kiekkohamsteri.model.GroupRequest.Type.*;
import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_GROUP_ADMIN;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class GroupServiceTest {
    @Mock
    GroupRepository groupRepository;

    @Mock
    GroupRequestRepository groupRequestRepository;

    @Mock
    RoleRepository roleRepository;

    @Captor
    ArgumentCaptor<GroupRequest> requestArgumentCaptor;

    private final Role GROUP_ADMIN_ROLE = new Role(USER_ROLE_GROUP_ADMIN, 1L);

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidRequest_whenRegisterRequest_thenSaveRequest() throws HttpResponseException {
        var source = new User(TEST_EMAIL);
        var target = new User(OTHER_EMAIL);

        var dto = GroupRequestDto.builder()
                .type(JOIN)
                .build();

        var group = new Group("group");
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        getGroupService().registerRequest(source, target, 1L, dto);

        verify(groupRepository, times(1)).findById(1L);
        verify(groupRequestRepository, times(1)).save(any(GroupRequest.class));
    }

    @Test
    void givenGroupNotFound_whenRegisterRequest_thenException() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            getGroupService().registerRequest(null, null, 1L, GroupRequestDto.builder().build());

            fail();
        } catch (HttpResponseException e) {
            assertEquals(SC_NOT_FOUND, e.getStatusCode());
        }
    }

    @ParameterizedTest
    @EnumSource(value = GroupRequest.Status.class, names = {"REQUESTED"}, mode = EnumSource.Mode.EXCLUDE)
    void givenRequestStatusNotRequested_whenCompleteRequest_thenException(GroupRequest.Status status) {
        try {
            var request = new GroupRequest(null, null, null, JOIN, status, null);
            getGroupService().completeRequest(request, true);

            fail();
        } catch (HttpResponseException e) {
            assertEquals(SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    void givenGroupAdminRoleNotFound_whenCompleteRequest_thenException() {
        var group = new Group("group");
        group.setId(1L);
        when(roleRepository.findByGroupId(1L)).thenReturn(Optional.empty());

        try {
            var request = new GroupRequest(group, null, null, JOIN, REQUESTED, null);
            getGroupService().completeRequest(request, true);

            fail();
        } catch (HttpResponseException e) {
            assertEquals(SC_NOT_FOUND, e.getStatusCode());
        }
    }

    @ParameterizedTest
    @EnumSource(value = GroupRequest.Type.class)
    void givenConfirmedFalse_whenCompleteRequest_thenRejectRequestAndKeepUserGroupsAndRoles(GroupRequest.Type type) throws HttpResponseException {
        var group = new Group("group");
        group.setId(1L);
        var user = new User(TEST_EMAIL);
        user.getGroups().add(group);
        user.getRoles().add(GROUP_ADMIN_ROLE);

        when(roleRepository.findByGroupId(1L)).thenReturn(Optional.of(GROUP_ADMIN_ROLE));

        var request = new GroupRequest(group, null, user, type, REQUESTED, null);
        var response = getGroupService().completeRequest(request, false);

        assertEquals(1, response.getGroups().size());
        assertEquals(1, response.getRoles().size());

        verify(groupRequestRepository, times(1)).save(requestArgumentCaptor.capture());
        var completedRequest = requestArgumentCaptor.getValue();
        assertEquals(REJECTED, completedRequest.getStatus());
    }

    @Test
    void givenJoinGroupRequest_whenCompleteRequest_thenAddGroup() throws HttpResponseException {
        var user = new User(TEST_EMAIL);

        var group = new Group("group");
        group.setId(1L);

        when(roleRepository.findByGroupId(1L)).thenReturn(Optional.of(GROUP_ADMIN_ROLE));

        var request = new GroupRequest(group, null, user, JOIN, REQUESTED, null);
        var response = getGroupService().completeRequest(request, true);

        assertEquals(1, response.getGroups().size());
        var responseGroup = response.getGroups().stream().findFirst().orElseThrow();
        assertEquals(group, responseGroup);

        verify(groupRequestRepository, times(1)).save(requestArgumentCaptor.capture());
        var completedRequest = requestArgumentCaptor.getValue();
        assertEquals(COMPLETED, completedRequest.getStatus());
    }

    @Test
    void givenKickFromGroupRequest_whenCompleteRequest_thenRemoveGroup() throws HttpResponseException {
        var group = new Group("group");
        group.setId(1L);
        var user = new User(TEST_EMAIL);
        user.getGroups().add(group);

        when(roleRepository.findByGroupId(1L)).thenReturn(Optional.of(GROUP_ADMIN_ROLE));

        var request = new GroupRequest(group, null, user, KICK, REQUESTED, null);
        var response = getGroupService().completeRequest(request, true);

        assertEquals(0, response.getGroups().size());

        verify(groupRequestRepository, times(1)).save(requestArgumentCaptor.capture());
        var completedRequest = requestArgumentCaptor.getValue();
        assertEquals(COMPLETED, completedRequest.getStatus());
    }

    @Test
    void givenPromoteUser_whenCompleteRequest_thenAddGroupAdminRole() throws HttpResponseException {
        var user = new User(TEST_EMAIL);

        var group = new Group("group");
        group.setId(1L);

        when(roleRepository.findByGroupId(1L)).thenReturn(Optional.of(GROUP_ADMIN_ROLE));

        var request = new GroupRequest(group, null, user, PROMOTE, REQUESTED, null);
        var response = getGroupService().completeRequest(request, true);

        assertEquals(1, response.getRoles().size());
        var role = response.getRoles().stream().findFirst().orElseThrow();
        assertEquals(GROUP_ADMIN_ROLE, role);

        verify(groupRequestRepository, times(1)).save(requestArgumentCaptor.capture());
        var completedRequest = requestArgumentCaptor.getValue();
        assertEquals(COMPLETED, completedRequest.getStatus());
    }

    @Test
    void givenDemoteUser_whenCompleteRequest_thenRemoveGroupAdminRole() throws HttpResponseException {
        var user = new User(TEST_EMAIL);
        user.getRoles().add(GROUP_ADMIN_ROLE);

        var group = new Group("group");
        group.setId(1L);

        when(roleRepository.findByGroupId(1L)).thenReturn(Optional.of(GROUP_ADMIN_ROLE));

        var request = new GroupRequest(group, null, user, DEMOTE, REQUESTED, null);
        var response = getGroupService().completeRequest(request, true);

        assertEquals(0, response.getRoles().size());

        verify(groupRequestRepository, times(1)).save(requestArgumentCaptor.capture());
        var completedRequest = requestArgumentCaptor.getValue();
        assertEquals(COMPLETED, completedRequest.getStatus());
    }

    @Test
    void givenNullOrInvalidDto_whenCreateGroup_thenException() {
        var user = new User(TEST_EMAIL);

        try {
            getGroupService().createGroup(user, null);

            fail();
        } catch (HttpResponseException e) {
            assertEquals(SC_BAD_REQUEST, e.getStatusCode());
        }

        var invalidDto = GroupCreateDto.builder().build();
        try {
            getGroupService().createGroup(user, invalidDto);

            fail();
        } catch (HttpResponseException e) {
            assertEquals(SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    void givenValidDto_whenCreateGroup_thenCreateGroupAndUpdateUser() throws HttpResponseException {
        var user = new User(TEST_EMAIL);

        var dto = GroupCreateDto.builder().name("group").build();
        getGroupService().createGroup(user, dto);

        verify(groupRepository, times(1)).save(any(Group.class));
        verify(roleRepository, times(1)).save(any(Role.class));

        assertEquals(1, user.getGroups().size());
        var group = user.getGroups().stream().findFirst().orElseThrow();
        assertEquals("group", group.getName());

        assertEquals(1, user.getRoles().size());
        var groupAdminRole = user.getRoles().stream().findFirst().orElseThrow();
        assertEquals(USER_ROLE_GROUP_ADMIN, groupAdminRole.getName());
        assertEquals(group.getId(), groupAdminRole.getGroupId());
    }

    GroupService getGroupService() { return new GroupService(groupRepository, groupRequestRepository, roleRepository); }
}
