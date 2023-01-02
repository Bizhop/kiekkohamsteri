package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import fi.bizhop.kiekkohamsteri.model.Disc;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.projection.v1.DiscProjection;

public interface DiscRepository extends PagingAndSortingRepository<Disc, Long>, JpaSpecificationExecutor<Disc> {
	Page<Disc> getByOwnerAndLostFalse(User user, Pageable pageable);
	@Deprecated
	List<DiscProjection> findByOwnerInAndPublicDiscTrue(List<User> users);
	@Deprecated
	Page<DiscProjection> findByLostTrue(Pageable pageable);

	@Deprecated
	DiscProjection getDiscById(Long id);

	@Deprecated
	Page<DiscProjection> findByForSaleTrue(Pageable pageable);
	Page<Disc> getByForSaleTrue(Pageable pageable);
	
	Integer countByCreatedAtBetween(Date begin, Date end);
	Integer countByOwner(User user);
}