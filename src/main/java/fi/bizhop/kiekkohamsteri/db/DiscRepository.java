package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.model.Disc;
import fi.bizhop.kiekkohamsteri.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DiscRepository extends PagingAndSortingRepository<Disc, Long>, JpaSpecificationExecutor<Disc> {
	Page<Disc> getByOwnerAndLostFalse(User user, Pageable pageable);
	Page<Disc> getByLostTrue(Pageable pageable);
	Page<Disc> getByForSaleTrue(Pageable pageable);
	
	Integer countByCreatedAtBetween(Date begin, Date end);
	Integer countByOwner(User user);

	@Override
	@Nonnull
	List<Disc> findAll();

	Optional<Disc> findByUuid(String uuid);
}
