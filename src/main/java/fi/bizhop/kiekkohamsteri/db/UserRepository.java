package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.v1.LeaderProjection;

public interface UserRepository extends CrudRepository<Members, Long> {
	Members findByEmail(String email);
	List<Members> findAllByOrderById();
	List<LeaderProjection> findByPublicDiscCountTrueOrderByDiscCountDesc();
	List<Members> findByPublicListTrue();

	//TODO: move this elsewhere, disc repo?
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update Kiekot k set k.publicDisc = true where k.member = ?1")
	void makeDiscsPublic(Members user);

	//TODO: move this as part of user update
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update Members u set u.level = ?2 where u.id = ?1")
	void updateUserLevel(Long id, Integer level);
	
	Integer countByCreatedAtBetween(Date beginDate, Date endDate);
}