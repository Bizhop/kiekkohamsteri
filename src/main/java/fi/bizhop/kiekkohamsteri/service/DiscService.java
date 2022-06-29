package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.DiscRepository;
import fi.bizhop.kiekkohamsteri.dto.DiscDto;
import fi.bizhop.kiekkohamsteri.dto.ListingDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.*;
import fi.bizhop.kiekkohamsteri.projection.v1.DiscProjection;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DiscService {
	
	private final DiscRepository discRepo;

	public DiscProjection newDisc(Members owner, R_mold defaultMold, R_muovi defaultPlastic, R_vari defaultColor) {
		var disc = new Kiekot(owner, defaultMold, defaultPlastic, defaultColor);
		if(owner.getPublicList()) {
			disc.setPublicDisc(true);
		}
		disc = discRepo.save(disc);
		
		return discRepo.getKiekotById(disc.getId());
	}
	
	public DiscProjection updateImage(Long id, String image) {
		var disc = discRepo.findById(id).orElseThrow();
		disc.setKuva(image);
		disc = discRepo.save(disc);
		return discRepo.getKiekotById(disc.getId());
	}
	
	public void deleteDisc(Long id, Members owner) throws AuthorizationException {
		var disc = discRepo.findById(id).orElse(null);
		
		if(disc == null || !disc.getMember().equals(owner)) {
			throw new AuthorizationException();
		}
		
		discRepo.deleteById(id);
	}
	
	public DiscProjection updateDisc(DiscDto dto, Long id, Members owner, R_mold newMold, R_muovi newPlastic, R_vari newColor) throws AuthorizationException, HttpResponseException {
		if(dto == null) throw new HttpResponseException(HttpServletResponse.SC_BAD_REQUEST, "Dto must not be null");
		var disc = discRepo.findById(id).orElse(null);
		
		if(disc == null || !disc.getMember().equals(owner)) {
			throw new AuthorizationException();
		}
		
		String[] ignoreRelations = {"member", "muovi", "mold", "vari"};
		var ignoreNulls = Utils.getNullPropertyNames(dto);
		var ignores = Stream.concat(Arrays.stream(ignoreRelations), Arrays.stream(ignoreNulls)).toArray(String[]::new);
		
		BeanUtils.copyProperties(dto, disc, ignores);
		
		disc.setMold(newMold);
		disc.setMuovi(newPlastic);
		disc.setVari(newColor);

		if(Boolean.TRUE.equals(dto.getLost())) {
			disc.setItb(false);
			disc.setMyynnissa(false);
		}

		discRepo.save(disc);
		return discRepo.getKiekotById(id);
	}

	public DiscProjection getDisc(Members owner, Long id) throws AuthorizationException {
		var disc = discRepo.getKiekotById(id);
		if(owner.getEmail().equals(disc.getOwnerEmail())) {
			return disc;
		}
		else {
			throw new AuthorizationException();
		}
	}

	public DiscProjection getDiscIfPublicOrOwn(Members owner, Long id) throws AuthorizationException {
		var disc = discRepo.getKiekotById(id);
		if(owner.getEmail().equals(disc.getOwnerEmail()) ||
				Boolean.TRUE.equals(disc.getPublicDisc())) {
			return disc;
		}
		else {
			throw new AuthorizationException();
		}
	}

	public List<ListingDto> getPublicLists(List<Members> usersWithPublicDiscs) {
		if(usersWithPublicDiscs == null || usersWithPublicDiscs.isEmpty()) return Collections.emptyList();

		return discRepo.findByMemberInAndPublicDiscTrue(usersWithPublicDiscs)
				.stream()
				.collect(Collectors.groupingBy(DiscProjection::getOwnerEmail))
				.entrySet().stream()
				.map(ListingDto::fromMapEntry)
				.collect(Collectors.toList());
	}
	
	public void handleFoundDisc(Members user, Long id) throws HttpResponseException {
		var disc = discRepo.findById(id).orElseThrow();
		if(!disc.getMember().equals(user)) {
			throw new HttpResponseException(HttpServletResponse.SC_FORBIDDEN, "User is not disc owner");
		}
		else if(!Boolean.TRUE.equals(disc.getLost())) {
			throw new HttpResponseException(HttpServletResponse.SC_BAD_REQUEST, "Disc is not lost");
		}
		else {
			disc.setLost(false);
			disc.setMyynnissa(false);
			disc.setItb(false);
			discRepo.save(disc);
		}
	}

	public void updateDiscCounts(List<Members> users) {
		for(var user : users) {
			var count = discRepo.countByMember(user);
			user.setDiscCount(count);
		}
	}

	// Passthrough methods to db
	// Not covered (or to be covered by unit tests)

	public Page<DiscProjection> getLost(Pageable pageable) {
		return discRepo.findByLostTrue(pageable);
	}

	public Page<DiscProjection> getDiscs(Members owner, Pageable pageable) {
		return discRepo.findByMemberAndLostFalse(owner, pageable);
	}

	public Page<DiscProjection> getDiscsForSale(Pageable pageable) {
		return discRepo.findByMyynnissaTrue(pageable);
	}

	//Forced method without owner check. Use with care.
	public void deleteDiscById(Long id) {
		discRepo.deleteById(id);
	}

	public Optional<Kiekot> getDiscDb(Long id) {
		return discRepo.findById(id);
	}

	public void saveDisc(Kiekot disc) {
		discRepo.save(disc);
	}
}
