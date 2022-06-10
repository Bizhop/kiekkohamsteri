package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fi.bizhop.kiekkohamsteri.model.R_mold;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.MoldDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.MoldProjection;

public interface MoldRepository extends PagingAndSortingRepository<R_mold, Long> {
	List<MoldDropdownProjection> findAllByOrderByKiekkoAsc();
	List<MoldDropdownProjection> findByValmistajaOrderByKiekkoAsc(R_valm valmistaja);
	
	Page<MoldProjection> findAllProjectedBy(Pageable pageable);
	Page<MoldProjection> findByValmistaja(R_valm valm, Pageable pageable);
	Integer countByCreatedAtBetween(Date beginDate, Date endDate);

	MoldProjection getR_moldById(Long id);
}