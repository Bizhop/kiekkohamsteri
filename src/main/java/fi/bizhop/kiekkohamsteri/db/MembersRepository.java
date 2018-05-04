package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.LeaderProjection;

public interface MembersRepository extends CrudRepository<Members, Long> {
	Members findByEmail(String email);
	List<Members> findAllByOrderById();
	List<Members> findByPublicDiscCountTrue();
	List<LeaderProjection> findByPublicDiscCountTrueOrderByDiscCountDesc();
	List<Members> findByPublicListTrue();
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update Kiekot k set k.publicDisc = true where k.member = ?1")
	void makeDiscsPublic(Members user);
}