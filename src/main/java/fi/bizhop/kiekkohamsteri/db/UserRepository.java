package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;

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

	//TODO: move this as part of user update
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update User u set u.level = ?2 where u.id = ?1")
	void updateUserLevel(Long id, Integer level);
	
	Integer countByCreatedAtBetween(Date beginDate, Date endDate);
}