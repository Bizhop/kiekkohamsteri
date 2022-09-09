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
	List<LeaderProjection> findByPublicDiscCountTrueOrderByDiscCountDesc();
	List<User> findByPublicListTrue();

	//TODO: move this elsewhere, disc repo?
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update Disc k set k.publicDisc = true where k.owner = ?1")
	void makeDiscsPublic(User user);
	
	Integer countByCreatedAtBetween(Date beginDate, Date endDate);

	List<User> findAllByGroups(Group group);

	List<User> findAllByRoles(Role role);
}