package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.KiekkoProjection;

public interface KiekkoRepository extends PagingAndSortingRepository<Kiekot, Long> {
	Page<KiekkoProjection> findByMember(Members member, Pageable pageable);
	List<Kiekot> findByMember(Members member);

	KiekkoProjection findById(Long id);

	Page<KiekkoProjection> findByMyynnissaTrue(Pageable pageable);
}