package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.model.Group;
import fi.bizhop.kiekkohamsteri.model.GroupRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupRequestRepository extends CrudRepository<GroupRequest, Long> {
    List<GroupRequest> findAll();
    List<GroupRequest> findByGroup(Group group);
}
