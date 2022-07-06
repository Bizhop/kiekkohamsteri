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

import static fi.bizhop.kiekkohamsteri.model.Ostot.Status.*;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Service
@RequiredArgsConstructor
public class BuyService {
	final BuyRepository buyRepo;

	public Ostot buyDisc(Members buyer, Kiekot disc) throws HttpResponseException {
		if(disc == null || !disc.getMyynnissa()) throw new HttpResponseException(SC_FORBIDDEN, "Not for sale");
		if(buyer.equals(disc.getMember())) throw new HttpResponseException(SC_BAD_REQUEST, "You can't buy your own disc");

		var existing = buyRepo.findByKiekkoAndOstajaAndStatus(disc, buyer, REQUESTED);
		if(existing != null) throw new HttpResponseException(SC_BAD_REQUEST, "You are already buying this disc");

		var buy = new Ostot(disc, disc.getMember(), buyer, REQUESTED);
		return buyRepo.save(buy);
	}

	public BuysDto getSummary(Members user) {
		var asSeller = buyRepo.findByStatusAndMyyja(REQUESTED, user);
		var asBuyer = buyRepo.findByStatusAndOstaja(REQUESTED, user);
		return BuysDto.builder()
				.myyjana(asSeller)
				.ostajana(asBuyer)
				.build();
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
				o.setStatus(REJECTED);
			}
			buyRepo.saveAll(otherBuys);
		}

		//change owner
		var disc = buy.getKiekko();
		disc.setMember(buy.getOstaja());
		disc.setMyynnissa(false);
		disc.setItb(false);

		//set status to confirmed
		buy.setStatus(CONFIRMED);
		buyRepo.save(buy);

		return disc;
	}
	
	public void reject(Long id, Members user) throws AuthorizationException {
		var buy = buyRepo.findById(id).orElseThrow();
		if(!user.equals(buy.getMyyja()) && !user.equals(buy.getOstaja())) throw new AuthorizationException();

		buy.setStatus(REJECTED);
		buyRepo.save(buy);
	}
}
