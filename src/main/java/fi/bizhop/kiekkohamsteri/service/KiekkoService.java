package fi.bizhop.kiekkohamsteri.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.KiekkoRepository;
import fi.bizhop.kiekkohamsteri.db.MembersRepository;
import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.db.MuoviRepository;
import fi.bizhop.kiekkohamsteri.db.VariRepository;
import fi.bizhop.kiekkohamsteri.dto.KiekkoDto;
import fi.bizhop.kiekkohamsteri.dto.ListausDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.model.R_mold;
import fi.bizhop.kiekkohamsteri.model.R_muovi;
import fi.bizhop.kiekkohamsteri.model.R_vari;
import fi.bizhop.kiekkohamsteri.projection.KiekkoProjection;
import fi.bizhop.kiekkohamsteri.util.Utils;

@Service
public class KiekkoService {
	
	@Autowired
	private KiekkoRepository kiekkoRepo;
	@Autowired
	private MoldRepository moldRepo;
	@Autowired
	private MuoviRepository muoviRepo;
	@Autowired
	private VariRepository variRepo;
	@Autowired
	private MembersRepository membersRepo;

	public Page<KiekkoProjection> haeKiekot(Members owner, Pageable pageable) {
		return kiekkoRepo.findByMemberAndLostFalse(owner, pageable);
	}
	
	public KiekkoProjection uusiKiekko(Members owner) {
		R_mold defaultMold = moldRepo.findOne(893L);
		R_muovi defaultMuovi = muoviRepo.findOne(13L);
		R_vari defaultVari = variRepo.findOne(1L);
		
		Kiekot kiekko = new Kiekot(owner, defaultMold, defaultMuovi, defaultVari);
		if(owner.getPublicList()) {
			kiekko.setPublicDisc(true);
		}
		kiekko = kiekkoRepo.save(kiekko);
		
		owner.addDisc();
		membersRepo.save(owner);
		
		return kiekkoRepo.findById(kiekko.getId());
	}
	
	public KiekkoProjection paivitaKuva(Long id, String kuva) {
		Kiekot kiekko = kiekkoRepo.findOne(id);
		kiekko.setKuva(kuva);
		kiekko = kiekkoRepo.save(kiekko);
		return kiekkoRepo.findById(kiekko.getId());
	}
	
	public void poistaKiekko(Long id, Members owner) throws AuthorizationException {
		Kiekot kiekko = kiekkoRepo.findOne(id);
		
		if(kiekko == null || !kiekko.getMember().equals(owner)) {
			throw new AuthorizationException();
		}
		
		kiekkoRepo.delete(id);
		
		owner.removeDisc();
		membersRepo.save(owner);
	}
	
	public KiekkoProjection paivitaKiekko(KiekkoDto dto, Long id, Members owner) throws AuthorizationException {
		Kiekot kiekko = kiekkoRepo.findOne(id);
		
		if(kiekko == null || !kiekko.getMember().equals(owner)) {
			throw new AuthorizationException();
		}
		
		String[] ignoreRelations = {"member", "muovi", "mold", "vari"};
		String[] ignoreNulls = Utils.getNullPropertyNames(dto);
		String[] ignores = Stream.concat(Arrays.stream(ignoreRelations), Arrays.stream(ignoreNulls)).toArray(String[]::new);
		
		BeanUtils.copyProperties(dto, kiekko, ignores);
		
		if(dto.getMoldId() != null) {
			R_mold mold = moldRepo.findOne(dto.getMoldId());
			kiekko.setMold(mold);
		}
		if(dto.getMuoviId() != null) {
			R_muovi muovi = muoviRepo.findOne(dto.getMuoviId());
			kiekko.setMuovi(muovi);
		}
		if(dto.getVariId() != null) {
			R_vari vari = variRepo.findOne(dto.getVariId());
			kiekko.setVari(vari);
		}
		
		kiekkoRepo.save(kiekko);
		return kiekkoRepo.findById(id);
	}

	public Page<KiekkoProjection> haeMyytavat(Pageable pageable) {
		return kiekkoRepo.findByMyynnissaTrue(pageable);
	}

	public KiekkoProjection haeKiekko(Members owner, Long id) throws AuthorizationException {
		KiekkoProjection kiekko = kiekkoRepo.findById(id);
		if(owner.getUsername().equals(kiekko.getOmistaja()) || kiekko.getPublicDisc()) {
			return kiekko;
		}
		else {
			throw new AuthorizationException();
		}
	}

	public List<ListausDto> haeJulkisetListat(Pageable pageable) {
		List<Members> julkiset = membersRepo.findByPublicListTrue();
		
		List<ListausDto> response = new ArrayList<ListausDto>();
		
		for(Members m : julkiset) {
			Page<KiekkoProjection> pages = kiekkoRepo.findByMemberAndPublicDiscTrue(m, pageable);
			ListausDto listaus = new ListausDto(pages.getContent());
			listaus.setUsername(m.getUsername());
			response.add(listaus);
		}
		
		return response;
	}
	
	public Page<KiekkoProjection> getLost(Pageable pageable) {
		return kiekkoRepo.findByLostTrue(pageable);
	}
	
	public DiscFoundStatus found(Members user, Long id) {
		Kiekot k = kiekkoRepo.findOne(id);
		if(k.getMember() != user) {
			return DiscFoundStatus.NOT_OWNED;
		}
		else if(!k.getLost()) {
			return DiscFoundStatus.NOT_LOST;
		}
		else {
			k.setLost(false);
			k.setMyynnissa(false);
			k.setItb(false);
			kiekkoRepo.save(k);
			return DiscFoundStatus.COMPLETED;
		}
	}
	
	public enum DiscFoundStatus {
		NOT_LOST, NOT_OWNED, COMPLETED
	}
}
