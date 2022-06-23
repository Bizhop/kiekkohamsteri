package fi.bizhop.kiekkohamsteri.service;

import java.util.List;
import java.util.stream.Collectors;

import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.BuyRepository;
import fi.bizhop.kiekkohamsteri.dto.BuysDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.model.Ostot;
import fi.bizhop.kiekkohamsteri.model.Ostot.Status;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class BuyService {
	final BuyRepository buyRepo;

	public Ostot buyDisc(Members buyer, Kiekot disc) throws HttpResponseException {
		if(buyer.equals(disc.getMember())) throw new HttpResponseException(HttpServletResponse.SC_BAD_REQUEST, "Et voi ostaa omaa kiekkoasi");
		if(!disc.getMyynnissa()) throw new HttpResponseException(HttpServletResponse.SC_FORBIDDEN, "Ei myynnissä");

		var existing = buyRepo.findByKiekkoAndOstajaAndStatus(disc, buyer, Status.REQUESTED);
		if(existing != null) throw new HttpResponseException(HttpServletResponse.SC_BAD_REQUEST, "Olet jo ostamassa tätä kiekkoa");

		var buy = new Ostot(disc, disc.getMember(), buyer, Status.REQUESTED);
		return buyRepo.save(buy);
	}

	public BuysDto getSummary(Members user) {
		return new BuysDto(
				buyRepo.findByStatusAndMyyja(Status.REQUESTED, user),
				buyRepo.findByStatusAndOstaja(Status.REQUESTED, user)
				);
	}

	public List<Ostot> getListing(Status status) {
		return status == null ? buyRepo.findAll() : buyRepo.findByStatus(status);
	}

	//confirm method returns the disc! It needs to be saved afterwards
	public Kiekot confirm(Long id, Members user) throws AuthorizationException {
		var buy = buyRepo.findById(id).orElseThrow();
		if(user != buy.getMyyja()) throw new AuthorizationException();

		//reject others
		var buys = buyRepo.findByKiekko(buy.getKiekko());
		var otherBuys = buys.stream()
				.filter(b -> !b.equals(buy))
				.collect(Collectors.toList());
		if(otherBuys.size() > 0) {
			for(Ostot o : otherBuys) {
				o.setStatus(Status.REJECTED);
			}
			buyRepo.saveAll(otherBuys);
		}

		//change owner
		var disc = buy.getKiekko();
		disc.setMember(buy.getOstaja());
		disc.setMyynnissa(false);
		disc.setItb(false);

		//set status to confirmed
		buy.setStatus(Status.CONFIRMED);
		buyRepo.save(buy);

		return disc;
	}
	
	public void reject(Long id, Members user) throws AuthorizationException {
		var buy = buyRepo.findById(id).orElseThrow();
		if(!user.equals(buy.getMyyja()) && !user.equals(buy.getOstaja())) throw new AuthorizationException();

		buy.setStatus(Status.REJECTED);
		buyRepo.save(buy);
	}
}
