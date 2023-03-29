package fi.bizhop.kiekkohamsteri.service;

import java.util.List;
import java.util.stream.Collectors;

import fi.bizhop.kiekkohamsteri.dto.v2.out.BuyOutputDto;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.BuyRepository;
import fi.bizhop.kiekkohamsteri.dto.v2.out.BuySummaryDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.model.Disc;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.model.Buy;
import fi.bizhop.kiekkohamsteri.model.Buy.Status;

import static fi.bizhop.kiekkohamsteri.model.Buy.Status.*;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Service
@RequiredArgsConstructor
public class BuyService {
	final BuyRepository buyRepo;

	public Buy buyDisc(User buyer, Disc disc) throws HttpResponseException {
		if(disc == null || !disc.getForSale()) throw new HttpResponseException(SC_FORBIDDEN, "Not for sale");
		if(buyer.equals(disc.getOwner())) throw new HttpResponseException(SC_BAD_REQUEST, "You can't buy your own disc");

		var existing = buyRepo.findByDiscAndBuyerAndStatus(disc, buyer, REQUESTED);
		if(existing != null) throw new HttpResponseException(SC_BAD_REQUEST, "You are already buying this disc");

		var buy = new Buy(disc, disc.getOwner(), buyer, REQUESTED);
		return buyRepo.save(buy);
	}

	public BuySummaryDto getSummary(User user) {
		var asSeller = buyRepo.findByStatusAndSeller(REQUESTED, user);
		var asBuyer = buyRepo.findByStatusAndBuyer(REQUESTED, user);
		return BuySummaryDto.builder()
				.asBuyer(asBuyer.stream().map(BuyOutputDto::fromDb).collect(Collectors.toList()))
				.asSeller(asSeller.stream().map(BuyOutputDto::fromDb).collect(Collectors.toList()))
				.build();
	}

	public List<Buy> getListing(Status status) {
		return status == null ? buyRepo.findAll() : buyRepo.findByStatus(status);
	}

	//confirm method returns the disc! It needs to be saved afterwards
	public Disc confirm(Long id, User user) throws AuthorizationException {
		var buy = buyRepo.findById(id).orElseThrow();
		if(user != buy.getSeller()) throw new AuthorizationException();

		//reject others
		var buys = buyRepo.findByDisc(buy.getDisc());
		var otherBuys = buys.stream()
				.filter(b -> !b.equals(buy))
				.collect(Collectors.toList());
		if(otherBuys.size() > 0) {
			for(Buy o : otherBuys) {
				o.setStatus(REJECTED);
			}
			buyRepo.saveAll(otherBuys);
		}

		//change owner
		var disc = buy.getDisc();
		disc.setOwner(buy.getBuyer());
		disc.setForSale(false);
		disc.setItb(false);

		//set status to confirmed
		buy.setStatus(CONFIRMED);
		buyRepo.save(buy);

		return disc;
	}
	
	public void reject(Long id, User user) throws AuthorizationException {
		var buy = buyRepo.findById(id).orElseThrow();
		if(!user.equals(buy.getSeller()) && !user.equals(buy.getBuyer())) throw new AuthorizationException();

		buy.setStatus(REJECTED);
		buyRepo.save(buy);
	}
}
