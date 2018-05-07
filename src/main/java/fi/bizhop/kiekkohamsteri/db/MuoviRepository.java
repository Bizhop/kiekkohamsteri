package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fi.bizhop.kiekkohamsteri.model.R_muovi;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.projection.MuoviDropdownProjection;
import fi.bizhop.kiekkohamsteri.projection.MuoviProjection;

public interface MuoviRepository extends PagingAndSortingRepository<R_muovi, Long> {
	List<MuoviDropdownProjection> findAllByOrderByMuoviAsc();
	List<MuoviDropdownProjection> findByValmistajaOrderByMuoviAsc(R_valm valm);
	
	Page<MuoviProjection> findAllProjectedBy(Pageable pageable);
	Page<MuoviProjection> findByValmistaja(R_valm valm, Pageable pageable);
	Integer countByCreatedAtBetween(Date beginDate, Date endDate);
}