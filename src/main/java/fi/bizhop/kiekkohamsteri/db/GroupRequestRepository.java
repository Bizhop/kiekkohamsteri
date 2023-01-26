package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.model.Group;
import fi.bizhop.kiekkohamsteri.model.GroupRequest;
import fi.bizhop.kiekkohamsteri.model.GroupRequest.Status;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface GroupRequestRepository extends CrudRepository<GroupRequest, Long> {
    List<GroupRequest> findByStatus(Status status);
    List<GroupRequest> findAllByGroupInAndStatus(Iterable<Group> groups, Status status);
    List<GroupRequest> findAllByGroup(Group group);
}
