package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.model.Ostot;
import fi.bizhop.kiekkohamsteri.model.Ostot.Status;

public interface OstoRepository extends CrudRepository<Ostot, Long> {
	List<Ostot> findAll();
	
	List<Ostot> findByStatus(Status status);
	
	List<Ostot> findByKiekko(Kiekot kiekko);
	
	List<Ostot> findByStatusAndMyyja(Status status, Members myyja);
	List<Ostot> findByStatusAndOstaja(Status status, Members ostaja);

	Integer countByUpdatedAtBetweenAndStatus(Date beginDate, Date endDate, Status confirmed);
}
