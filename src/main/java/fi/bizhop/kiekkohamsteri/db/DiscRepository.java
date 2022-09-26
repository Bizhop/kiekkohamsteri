package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fi.bizhop.kiekkohamsteri.model.Disc;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.projection.v1.DiscProjection;

public interface DiscRepository extends PagingAndSortingRepository<Disc, Long> {
	Page<DiscProjection> findByOwnerAndLostFalse(User user, Pageable pageable);
	List<Disc> findByOwnerAndLostFalse(User user);
	List<DiscProjection> findByOwnerInAndPublicDiscTrue(List<User> users);
	Page<DiscProjection> findByLostTrue(Pageable pageable);

	DiscProjection getDiscById(Long id);

	Page<DiscProjection> findByForSaleTrue(Pageable pageable);
	
	Integer countByCreatedAtBetween(Date begin, Date end);
	Integer countByOwner(User user);
}