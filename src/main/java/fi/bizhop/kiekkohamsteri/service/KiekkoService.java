package fi.bizhop.kiekkohamsteri.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.KiekkoRepository;
import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.db.MuoviRepository;
import fi.bizhop.kiekkohamsteri.db.VariRepository;
import fi.bizhop.kiekkohamsteri.dto.KiekkoDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.model.R_mold;
import fi.bizhop.kiekkohamsteri.model.R_muovi;
import fi.bizhop.kiekkohamsteri.model.R_vari;
import fi.bizhop.kiekkohamsteri.projection.KiekkoProjection;

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

	public List<KiekkoProjection> haeKiekot(Members owner) {
		return kiekkoRepo.findByMember(owner);
	}
	
	public KiekkoProjection uusiKiekko(Members owner) {
		R_mold defaultMold = moldRepo.findOne(568L);
		R_muovi defaultMuovi = muoviRepo.findOne(109L);
		R_vari defaultVari = variRepo.findOne(1L);
		
		Kiekot kiekko = new Kiekot(owner, defaultMold, defaultMuovi, defaultVari);
		kiekko = kiekkoRepo.save(kiekko);
		
		return kiekkoRepo.findById(kiekko.getId());
	}
	
	public KiekkoProjection paivitaKiekko(KiekkoDto dto, Long id, Members owner) throws AuthorizationException {
		Kiekot kiekko = kiekkoRepo.findOne(id);
		
		if(!kiekko.getMember().equals(owner)) {
			throw new AuthorizationException();
		}
		
		R_mold mold = moldRepo.findOne(dto.getMoldId());
		R_muovi muovi = muoviRepo.findOne(dto.getMuoviId());
		R_vari vari = variRepo.findOne(dto.getVariId());
		
		kiekko.setMold(mold)
			.setMuovi(muovi)
			.setVari(vari)
			.setDyed(dto.getDyed())
			.setHinta(dto.getHinta())
			.setHohto(dto.getHohto())
			.setItb(dto.getItb())
			.setKunto(dto.getKunto())
			.setKuva(dto.getKuva())
			.setLoytokiekko(dto.getLoytokiekko())
			.setMuuta(dto.getMuuta())
			.setMyynnissa(dto.getMyynnissa())
			.setPaino(dto.getPaino())
			.setSpessu(dto.getSpessu())
			.setSwirly(dto.getSwirly())
			.setTussit(dto.getTussit());
		
		kiekkoRepo.save(kiekko);
		return kiekkoRepo.findById(id);
	}
}
