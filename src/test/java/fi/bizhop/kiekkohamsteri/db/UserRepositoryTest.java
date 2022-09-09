package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.Group;
import fi.bizhop.kiekkohamsteri.model.Role;
import fi.bizhop.kiekkohamsteri.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.REPOSITORY;
import static fi.bizhop.kiekkohamsteri.TestObjects.OTHER_EMAIL;
import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_EMAIL;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static fi.bizhop.kiekkohamsteri.TestUtils.dateWithDayOffset;
import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_ADMIN;
import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_GROUP_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest extends SpringContextTestBase {
    @Autowired UserRepository userRepository;
    @Autowired GroupRepository groupRepository;
    @Autowired RoleRepository roleRepository;

    BaseAdder adder = new BaseAdder("user", REPOSITORY);

    @BeforeEach
    void setupTestData() {
        userRepository.deleteAll();
        groupRepository.deleteAll();
        roleRepository.deleteAll();

        var testUser = new User(TEST_EMAIL);
        var otherUser = new User(OTHER_EMAIL);

        var group1 = new Group();
        group1.setId(1L);
        group1.setName("group 1");
        group1 = groupRepository.save(group1);

        var group2 = new Group();
        group2.setId(2L);
        group2.setName("group 2");
        group2 = groupRepository.save(group2);

        var adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName(USER_ROLE_ADMIN);
        roleRepository.save(adminRole);

        var groupAdminRole = new Role();
        groupAdminRole.setId(2L);
        groupAdminRole.setName(USER_ROLE_GROUP_ADMIN);
        groupAdminRole.setGroupId(group2.getId());
        roleRepository.save(groupAdminRole);

        testUser.setUsername("test user");
        testUser.setDiscCount(55);
        testUser.setPublicDiscCount(true);
        testUser.setPublicList(true);
        testUser.setGroups(Set.of(group1));
        testUser.setRoles(Set.of(adminRole));

        otherUser.setUsername("other user");
        otherUser.setDiscCount(88);
        otherUser.setPublicDiscCount(false);
        otherUser.setPublicList(false);
        otherUser.setGroups(Set.of(group2));
        otherUser.setRoles(Set.of(groupAdminRole));

        userRepository.saveAll(List.of(testUser, otherUser));
    }

    @Test
    void findByEmailTest() {
        var testUser = userRepository.findByEmail(TEST_EMAIL);
        var otherUser = userRepository.findByEmail(OTHER_EMAIL);
        var none = userRepository.findByEmail("something.else@example.com");

        assertEquals(TEST_EMAIL, testUser.getEmail());
        assertEquals(OTHER_EMAIL, otherUser.getEmail());
        assertNull(none);
    }

    @Test
    void findByPublicDiscCountTrueOrderByDiscCountDescTest() {
        var result = userRepository.findByPublicDiscCountTrueOrderByDiscCountDesc();

        assertEqualsJson(adder.create("leaders.json"), result);
    }

    @Test
    void findByPublicListTrueTest() {
        var result = userRepository.findByPublicListTrue();

        assertEqualsJson(adder.create("publicListTrue.json"), result);
    }

    @Test
    void countByCreatedAtBetweenTest() {
        var today = new Date();
        var yesterday = dateWithDayOffset(today, -1);
        var tomorrow = dateWithDayOffset(today, 1);
        var dayAfterTomorrow = dateWithDayOffset(today, 2);

        var insideRange = userRepository.countByCreatedAtBetween(yesterday, tomorrow);
        var outsideRange = userRepository.countByCreatedAtBetween(tomorrow, dayAfterTomorrow);

        assertEquals(2, insideRange);
        assertEquals(0, outsideRange);
    }

    @Test
    void findAllByGroupsTest() {
        var group = groupRepository.findById(1L).orElseThrow();

        var result = userRepository.findAllByGroups(group);

        assertEquals(1, result.size());
        var user = result.get(0);
        assertEquals(TEST_EMAIL, user.getEmail());
    }

    @Test
    void findAllByRolesTest() {
        var adminRole = roleRepository.findById(1L).orElseThrow();
        var groupAdminRole = roleRepository.findById(2L).orElseThrow();

        var admins = userRepository.findAllByRoles(adminRole);
        var groupAdmins = userRepository.findAllByRoles(groupAdminRole);

        assertEquals(1, admins.size());
        var admin = admins.get(0);
        assertEquals(TEST_EMAIL, admin.getEmail());
        assertEquals(1, admin.getRoles().size());
        var adminUserRole = admin.getRoles().stream().findFirst().orElseThrow();
        assertEquals(USER_ROLE_ADMIN, adminUserRole.getName());

        assertEquals(1, groupAdmins.size());
        var groupAdmin = groupAdmins.get(0);
        assertEquals(OTHER_EMAIL, groupAdmin.getEmail());
        assertEquals(1, groupAdmin.getRoles().size());
        var groupAdminUserRole = groupAdmin.getRoles().stream().findFirst().orElseThrow();
        assertEquals(USER_ROLE_GROUP_ADMIN, groupAdminUserRole.getName());
        assertEquals(2L, groupAdminUserRole.getGroupId());
    }
}
