package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.KiekkoProjection;

public interface KiekkoRepository extends CrudRepository<Kiekot, Long> {
	List<KiekkoProjection> findByMember(Members member);

	KiekkoProjection findById(Long id);
}