package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.model.Group;
import fi.bizhop.kiekkohamsteri.model.GroupRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;

public interface GroupRepository extends CrudRepository<Group, Long> {
    @Override
    @NonNull
    List<Group> findAll();

    List<Group> findAllByIdIn(Set<Long> ids);
}
