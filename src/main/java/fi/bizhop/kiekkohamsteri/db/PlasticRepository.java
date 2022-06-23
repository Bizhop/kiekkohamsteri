package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fi.bizhop.kiekkohamsteri.model.R_muovi;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.v1.dropdown.PlasticDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.v1.PlasticProjection;

public interface PlasticRepository extends PagingAndSortingRepository<R_muovi, Long> {
	List<PlasticDropdownProjection> findAllByOrderByMuoviAsc();
	List<PlasticDropdownProjection> findByValmistajaOrderByMuoviAsc(R_valm valm);
	
	Page<PlasticProjection> findAllProjectedBy(Pageable pageable);
	Page<PlasticProjection> findByValmistaja(R_valm valm, Pageable pageable);
	Integer countByCreatedAtBetween(Date beginDate, Date endDate);

	PlasticProjection getR_muoviById(Long id);
}
