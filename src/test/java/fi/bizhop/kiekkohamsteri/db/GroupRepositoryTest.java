package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.model.Group;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles("test")
public class GroupRepositoryTest extends SpringContextTestBase {
    @Autowired GroupRepository groupRepository;

    @Test
    void testNameFieldUniqueness() {
        var group1 = new Group("TEST");
        groupRepository.save(group1);

        var group2 = new Group("TEST");
        try {
            groupRepository.save(group2);

            fail();
        } catch (DataIntegrityViolationException ignored) {}
    }
}
