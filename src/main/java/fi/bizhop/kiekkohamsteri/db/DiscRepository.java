package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.v1.DiscProjection;

public interface DiscRepository extends PagingAndSortingRepository<Kiekot, Long> {
	Page<DiscProjection> findByMemberAndLostFalse(Members member, Pageable pageable);
	List<DiscProjection> findByMemberInAndPublicDiscTrue(List<Members> memberList);
	Page<DiscProjection> findByLostTrue(Pageable pageable);

	DiscProjection getKiekotById(Long id);

	Page<DiscProjection> findByMyynnissaTrue(Pageable pageable);
	
	Integer countByCreatedAtBetween(Date begin, Date end);
	Integer countByMember(Members l);
}