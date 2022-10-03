package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.GroupRepository;
import fi.bizhop.kiekkohamsteri.db.RoleRepository;
import fi.bizhop.kiekkohamsteri.db.UserRepository;
import fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.Group;
import fi.bizhop.kiekkohamsteri.model.Role;
import fi.bizhop.kiekkohamsteri.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_EMAIL;
import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_ADMIN;
import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_GROUP_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void givenNormalUserRequest_whenUpdateDetails_thenSuccess() {
        var user = new User(TEST_EMAIL);

        var dto = UserUpdateDto.builder()
                .username("USER")
                .firstName("FIRST")
                .lastName("LAST")
                .pdgaNumber(12345)
                .build();

        getUserService().updateDetails(user, dto, false);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals("USER", user.getUsername());
        assertEquals("FIRST", user.getFirstName());
        assertEquals("LAST", user.getLastName());
        assertEquals(12345, user.getPdgaNumber());
    }

    @Test
    void givenAdminRequest_whenAddAndRemoveUserRole_thenSuccess() {
        var user = new User(TEST_EMAIL);
        var adminRole = new Role();
        adminRole.setName(USER_ROLE_ADMIN);

        var addDto = UserUpdateDto.builder()
                .addToRole(USER_ROLE_ADMIN)
                .build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));

        getUserService().updateDetails(user, addDto, true);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(1, user.getRoles().size());
        var role = user.getRoles().stream().findFirst().orElseThrow();
        assertEquals(USER_ROLE_ADMIN, role.getName());

        var removeDto = UserUpdateDto.builder()
                .removeFromRole(USER_ROLE_ADMIN)
                .build();

        getUserService().updateDetails(user, removeDto, true);

        verify(userRepository, times(2)).save(any(User.class));

        assertEquals(0, user.getRoles().size());
    }

    @Test
    void givenNonAdminRequest_whenAddUserRole_thenDontAddRole() {
        var user = new User(TEST_EMAIL);
        var adminRole = new Role();
        adminRole.setName(USER_ROLE_ADMIN);

        var addDto = UserUpdateDto.builder()
                .addToRole(USER_ROLE_ADMIN)
                .build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));

        getUserService().updateDetails(user, addDto, false);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(0, user.getRoles().size());
    }

    @Test
    void givenNonAdminRequest_whenUserLeavesGroup_thenSuccess() {
        var user = new User(TEST_EMAIL);
        var group = new Group("group 1");
        user.getGroups().add(group);
        var groupAdminRole = new Role(USER_ROLE_GROUP_ADMIN, 1L);
        user.getRoles().add(groupAdminRole);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(roleRepository.findByGroupId(1L)).thenReturn(Optional.of(groupAdminRole));

        var removeDto = UserUpdateDto.builder()
                .removeFromGroupId(1L)
                .build();

        getUserService().updateDetails(user, removeDto, false);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(0, user.getGroups().size());
        assertEquals(0, user.getRoles().size());
    }

    private UserService getUserService() {
        return new UserService(userRepository, groupRepository, roleRepository);
    }
}
