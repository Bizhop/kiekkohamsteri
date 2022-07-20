package fi.bizhop.kiekkohamsteri.db;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.Disc;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.model.Buy;
import fi.bizhop.kiekkohamsteri.model.Buy.Status;

import javax.annotation.Nonnull;

public interface BuyRepository extends CrudRepository<Buy, Long> {
	Buy findByDiscAndBuyerAndStatus(Disc disc, User buyer, Status status);

	@Override
	@Nonnull
	List<Buy> findAll();
	
	List<Buy> findByStatus(Status status);
	
	List<Buy> findByDisc(Disc disc);
	
	List<Buy> findByStatusAndSeller(Status status, User seller);
	List<Buy> findByStatusAndBuyer(Status status, User buyer);

	Integer countByUpdatedAtBetweenAndStatus(Date beginDate, Date endDate, Status status);
}
