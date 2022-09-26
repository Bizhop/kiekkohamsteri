package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;
import java.util.Set;

import fi.bizhop.kiekkohamsteri.model.Group;
import fi.bizhop.kiekkohamsteri.model.Role;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.projection.v1.LeaderProjection;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByEmail(String email);
	List<User> findAllByOrderById();

	Integer countByCreatedAtBetween(Date beginDate, Date endDate);

	List<User> findAllByGroups(Group group);

	List<User> findAllByRoles(Role role);
}