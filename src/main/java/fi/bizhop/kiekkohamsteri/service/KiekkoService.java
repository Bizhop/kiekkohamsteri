package fi.bizhop.kiekkohamsteri.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.KiekkoRepository;
import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.db.MuoviRepository;
import fi.bizhop.kiekkohamsteri.db.VariRepository;
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
}
