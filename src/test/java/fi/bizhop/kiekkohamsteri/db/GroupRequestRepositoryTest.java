package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.GroupRequest;
import fi.bizhop.kiekkohamsteri.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static fi.bizhop.kiekkohamsteri.model.GroupRequest.Status.REQUESTED;
import static fi.bizhop.kiekkohamsteri.model.GroupRequest.Type.JOIN;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class GroupRequestRepositoryTest extends SpringContextTestBase {
    @Autowired UserRepository userRepository;
    @Autowired GroupRepository groupRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired GroupRequestRepository groupRequestRepository;

    static boolean testDataInitialized = false;
    static User testUser = null;
    static User groupAdminUser = null;

    @BeforeEach
    void setupTestData() {
        //kind of dirty hack to make this run only once with autowired repos
        if (!testDataInitialized) {
            groupRepository.saveAll(GROUPS);
            roleRepository.saveAll(GROUP_ADMIN_USER.getRoles());

            testUser = userRepository.save(TEST_USER);
            groupAdminUser = userRepository.save(GROUP_ADMIN_USER);

            testDataInitialized = true;
        }
    }

    @Test
    void findAllByGroupInTest() {
        groupRequestRepository.save(new GroupRequest(GROUPS.get(0), TEST_USER, TEST_USER, JOIN, REQUESTED, "info"));
        groupRequestRepository.save(new GroupRequest(GROUPS.get(1), TEST_USER, TEST_USER, JOIN, REQUESTED, "info"));

        var result = groupRequestRepository.findAllByGroupInAndStatus(Set.of(GROUPS.get(0)), REQUESTED);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getGroup().getId());
    }
}
