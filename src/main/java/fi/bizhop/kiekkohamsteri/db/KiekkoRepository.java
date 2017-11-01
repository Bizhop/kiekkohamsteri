package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.KiekotListausProjection;

public interface KiekkoRepository extends CrudRepository<Kiekot, Long> {
	List<KiekotListausProjection> findByMember(Members member);

	KiekotListausProjection findById(Long id);
}