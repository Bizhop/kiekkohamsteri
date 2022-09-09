package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.GroupRepository;
import fi.bizhop.kiekkohamsteri.db.RoleRepository;
import fi.bizhop.kiekkohamsteri.db.UserRepository;
import fi.bizhop.kiekkohamsteri.dto.v1.in.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.Group;
import fi.bizhop.kiekkohamsteri.model.Role;
import fi.bizhop.kiekkohamsteri.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.TestObjects.OTHER_EMAIL;
import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_EMAIL;
import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_ADMIN;
import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_GROUP_ADMIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    GroupRepository groupRepository;

    @Mock
    RoleRepository roleRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }


    //V1

    @Test
    void givenPublicListFalse_whenUpdateDetails_thenUpdateDetails() {
        var user = new User(TEST_EMAIL);

        var dto = UserUpdateDto.builder()
                .etunimi("TEST")
                .sukunimi("USER")
                .publicList(false)
                .build();

        getUserService().updateDetails(user, dto, false);

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, never()).makeDiscsPublic(any());

        assertEquals("TEST", user.getFirstName());
        assertEquals("USER", user.getLastName());
        assertFalse(user.getPublicList());
    }

    @Test
    void givenPublicListTrue_whenUpdateDetails_thenUpdateDetailsAndMakeDiscsPublic() {
        var user = new User(TEST_EMAIL);

        var dto = UserUpdateDto.builder()
                .etunimi("TEST")
                .sukunimi("USER")
                .publicList(true)
                .build();

        getUserService().updateDetails(user, dto, false);

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).makeDiscsPublic(user);

        assertEquals("TEST", user.getFirstName());
        assertEquals("USER", user.getLastName());
        assertTrue(user.getPublicList());
    }

    @Test
    void givenAdminRequest_whenUpdateDetailsWithLevel_thenUpdateDetailsWithLevel() {
        var user = new User(TEST_EMAIL);

        var dto = UserUpdateDto.builder().level(2).build();

        getUserService().updateDetails(user, dto, true);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(2, user.getLevel());
    }

    @Test
    void givenNonAdminRequest_whenUpdateDetailsWithLevel_thenDontUpdateLevel() {
        var user = new User(TEST_EMAIL);

        var dto = UserUpdateDto.builder().level(2).build();

        getUserService().updateDetails(user, dto, false);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(1, user.getLevel());
    }


    //V2

    @Test
    void givenAdminRequest_whenAddAndRemoveUserRole_thenSuccess() {
        var user = new User(TEST_EMAIL);
        var adminRole = new Role();
        adminRole.setName(USER_ROLE_ADMIN);

        var addDto = fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto.builder()
                .addToRole(USER_ROLE_ADMIN)
                .build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));

        getUserService().updateDetailsV2(user, null, addDto, true);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(1, user.getRoles().size());
        var role = user.getRoles().stream().findFirst().orElseThrow();
        assertEquals(USER_ROLE_ADMIN, role.getName());

        var removeDto = fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto.builder()
                .removeFromRole(USER_ROLE_ADMIN)
                .build();

        getUserService().updateDetailsV2(user, null, removeDto, true);

        verify(userRepository, times(2)).save(any(User.class));

        assertEquals(0, user.getRoles().size());
    }

    @Test
    void givenNonAdminRequest_whenAddUserRole_thenDontAddRole() {
        var user = new User(TEST_EMAIL);
        var adminRole = new Role();
        adminRole.setName(USER_ROLE_ADMIN);

        var addDto = fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto.builder()
                .addToRole(USER_ROLE_ADMIN)
                .build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));

        getUserService().updateDetailsV2(user, null, addDto, false);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(0, user.getRoles().size());
    }

    @Test
    void givenNonAdminRequest_whenUserDropsRole_thenSuccess() {
        var user = new User(TEST_EMAIL);
        var adminRole = new Role();
        adminRole.setName(USER_ROLE_ADMIN);
        user.getRoles().add(adminRole);

        var removeDto = fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto.builder()
                .removeFromRole(USER_ROLE_ADMIN)
                .build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));

        getUserService().updateDetailsV2(user, user, removeDto, false);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(0, user.getRoles().size());
    }

    @Test
    void givenAdminRequest_whenAddAndRemoveUserGroup_thenSuccess() {
        var user = new User(TEST_EMAIL);
        var group1 = new Group();
        group1.setName("group 1");

        var addDto = fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto.builder()
                .addToGroupId(1L)
                .build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));

        getUserService().updateDetailsV2(user, null, addDto, true);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(1, user.getGroups().size());
        var group = user.getGroups().stream().findFirst().orElseThrow();
        assertEquals("group 1", group.getName());

        var removeDto = fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto.builder()
                .removeFromGroupId(1L)
                .build();

        getUserService().updateDetailsV2(user, null, removeDto, true);

        verify(userRepository, times(2)).save(any(User.class));

        assertEquals(0, user.getGroups().size());
    }

    @Test
    void givenNonAdminRequest_whenUserLeavesGroup_thenSuccess() {
        var user = new User(TEST_EMAIL);
        var group1 = new Group();
        group1.setName("group 1");
        user.getGroups().add(group1);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));

        var removeDto = fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto.builder()
                .removeFromGroupId(1L)
                .build();

        getUserService().updateDetailsV2(user, user, removeDto, false);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(0, user.getGroups().size());
    }

    @Test
    void givenGroupAdmin_whenAddAndRemoveUserGroup_thenSuccess() {
        var user = new User(TEST_EMAIL);

        var group1 = new Group();
        group1.setId(1L);
        group1.setName("group 1");

        var role = new Role();
        role.setName(USER_ROLE_GROUP_ADMIN);
        role.setGroupId(1L);

        var groupAdmin = new User(OTHER_EMAIL);
        groupAdmin.getGroups().add(group1);
        groupAdmin.getRoles().add(role);

        var addDto = fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto.builder()
                .addToGroupId(1L)
                .build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));

        getUserService().updateDetailsV2(user, groupAdmin, addDto, false);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(1, user.getGroups().size());
        var group = user.getGroups().stream().findFirst().orElseThrow();
        assertEquals("group 1", group.getName());

        var removeDto = fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto.builder()
                .removeFromGroupId(1L)
                .build();

        getUserService().updateDetailsV2(user, groupAdmin, removeDto, false);

        verify(userRepository, times(2)).save(any(User.class));

        assertEquals(0, user.getGroups().size());
    }

    private UserService getUserService() {
        return new UserService(userRepository, groupRepository, roleRepository);
    }
}
