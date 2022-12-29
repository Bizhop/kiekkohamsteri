package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.DiscRepository;
import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscInputDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscSearchDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.DiscOutputDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.*;
import fi.bizhop.kiekkohamsteri.projection.v1.DiscProjection;
import fi.bizhop.kiekkohamsteri.search.discs.DiscSpecificationBuilder;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static fi.bizhop.kiekkohamsteri.search.SearchOperation.EQUAL;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DiscService {

	private final DiscRepository discRepo;

	public DiscProjection newDisc(User owner, Mold defaultMold, Plastic defaultPlastic, Color defaultColor) {
		var disc = new Disc(owner, defaultMold, defaultPlastic, defaultColor);
		disc = discRepo.save(disc);

		return discRepo.getDiscById(disc.getId());
	}

	public DiscProjection updateImage(Long id, String image) {
		var disc = discRepo.findById(id).orElseThrow();
		disc.setImage(image);
		disc = discRepo.save(disc);
		return discRepo.getDiscById(disc.getId());
	}

	public void deleteDisc(Long id, User owner) throws AuthorizationException {
		var disc = discRepo.findById(id).orElse(null);

		if(disc == null || !disc.getOwner().equals(owner)) {
			throw new AuthorizationException();
		}

		discRepo.deleteById(id);
	}

	//V1 compatibility
	public DiscProjection updateDisc(fi.bizhop.kiekkohamsteri.dto.v1.in.DiscInputDto dto, Long id, User owner, Mold newMold, Plastic newPlastic, Color newColor) throws AuthorizationException {
		var inputV2 = DiscInputDto.fromV1(dto);

		updateDisc(inputV2, id, owner, newMold, newPlastic, newColor);
		return discRepo.getDiscById(id);
	}

	public DiscOutputDto updateDisc(DiscInputDto dto, Long id, User owner, Mold newMold, Plastic newPlastic, Color newColor) throws AuthorizationException {
		var disc = discRepo.findById(id).orElse(null);

		if(disc == null || !disc.getOwner().equals(owner)) {
			throw new AuthorizationException();
		}

		String[] ignoreRelations = {"owner", "plastic", "mold", "color"};
		var ignoreNulls = Utils.getNullPropertyNames(dto);
		var ignores = Stream.concat(
						Arrays.stream(ignoreRelations),
						ignoreNulls.stream())
				.toArray(String[]::new);

		BeanUtils.copyProperties(dto, disc, ignores);

		disc.setMold(newMold);
		disc.setPlastic(newPlastic);
		disc.setColor(newColor);

		if(Boolean.TRUE.equals(dto.getLost())) {
			disc.setItb(false);
			disc.setForSale(false);
		}

		return DiscOutputDto.fromDb(discRepo.save(disc));
	}

	public DiscProjection getDisc(User owner, Long id) throws AuthorizationException {
		var disc = discRepo.getDiscById(id);
		if(owner.getEmail().equals(disc.getOwnerEmail())) {
			return disc;
		}
		else {
			throw new AuthorizationException();
		}
	}

	public DiscProjection getDiscIfPublicOrOwn(User owner, Long id) throws AuthorizationException, HttpResponseException {
		var disc = discRepo.getDiscById(id);
		if(disc == null) throw new HttpResponseException(SC_NOT_FOUND, "Disc not found");
		if(owner.getEmail().equals(disc.getOwnerEmail()) ||
				Boolean.TRUE.equals(disc.getPublicDisc())) {
			return disc;
		}
		else {
			throw new AuthorizationException();
		}
	}

	public void handleFoundDisc(User user, Long id) throws HttpResponseException {
		var disc = discRepo.findById(id).orElse(null);
		if(disc == null || !disc.getOwner().equals(user)) {
			throw new HttpResponseException(HttpServletResponse.SC_FORBIDDEN, "User is not disc owner");
		}
		else if(!Boolean.TRUE.equals(disc.getLost())) {
			throw new HttpResponseException(HttpServletResponse.SC_BAD_REQUEST, "Disc is not lost");
		}
		else {
			disc.setLost(false);
			disc.setForSale(false);
			disc.setItb(false);
			discRepo.save(disc);
		}
	}

	public Page<Disc> search(User owner, Pageable pageable, DiscSearchDto searchDto) throws HttpResponseException {
		if(searchDto.getCriteria() == null) throw new HttpResponseException(SC_BAD_REQUEST, "Criteria must not be null");

		var builder = DiscSpecificationBuilder.builder();

		builder.with("owner", EQUAL, owner);
		builder.with("lost", EQUAL, Boolean.FALSE);
		searchDto.getCriteria().forEach(builder::with);

		return discRepo.findAll(builder.build(), pageable);
	}

	// Pass-through methods to db
	// Not covered (or to be covered by unit tests)

	public Page<DiscProjection> getLost(Pageable pageable) {
		return discRepo.findByLostTrue(pageable);
	}

	public Page<DiscProjection> getDiscs(User owner, Pageable pageable) {
		return discRepo.findByOwnerAndLostFalse(owner, pageable);
	}

	public Page<Disc> getDiscsV2(User owner, Pageable pageable) {
		return discRepo.getByOwnerAndLostFalse(owner, pageable);
	}

	public Page<DiscProjection> getDiscsForSale(Pageable pageable) {
		return discRepo.findByForSaleTrue(pageable);
	}

	//Forced method without owner check. Use with care.
	public void deleteDiscById(Long id) {
		discRepo.deleteById(id);
	}

	public Optional<Disc> getDiscDb(Long id) {
		return discRepo.findById(id);
	}

	public void saveDisc(Disc disc) {
		discRepo.save(disc);
	}
}
