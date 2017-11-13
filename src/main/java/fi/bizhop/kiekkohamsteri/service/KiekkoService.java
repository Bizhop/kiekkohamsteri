package fi.bizhop.kiekkohamsteri.service;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	public Page<KiekkoProjection> haeKiekot(Members owner, Pageable pageable) {
		return kiekkoRepo.findByMember(owner, pageable);
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
		
		String[] ignoreRelations = {"member", "muovi", "mold", "vari"};
		String[] ignoreNulls = getNullPropertyNames(dto);
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
	
	public static String[] getNullPropertyNames (Object source) {
	    final BeanWrapper src = new BeanWrapperImpl(source);
	    PropertyDescriptor[] pds = src.getPropertyDescriptors();

	    Set<String> emptyNames = new HashSet<String>();
	    for(PropertyDescriptor pd : pds) {
	        Object srcValue = src.getPropertyValue(pd.getName());
	        if (srcValue == null) emptyNames.add(pd.getName());
	    }
	    String[] result = new String[emptyNames.size()];
	    return emptyNames.toArray(result);
	}
}
