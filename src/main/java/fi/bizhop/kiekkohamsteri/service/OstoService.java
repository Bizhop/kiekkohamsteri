package fi.bizhop.kiekkohamsteri.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.KiekkoRepository;
import fi.bizhop.kiekkohamsteri.db.OstoRepository;
import fi.bizhop.kiekkohamsteri.dto.OstotDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.model.Ostot;
import fi.bizhop.kiekkohamsteri.model.Ostot.Status;

@Service
public class OstoService {
	@Autowired
	OstoRepository ostoRepo;
	@Autowired
	KiekkoRepository kiekkoRepo;

	public void ostaKiekko(Long id, Members user) {
		Kiekot kiekko = kiekkoRepo.findOne(id);
		
		Ostot osto = new Ostot(kiekko, kiekko.getMember(), user, Status.REQUESTED);
		ostoRepo.save(osto);
	}

	public List<Ostot> list() {
		return ostoRepo.findAll();
	}
	
	public OstotDto yhteenveto(Members user) {
		return new OstotDto(
				ostoRepo.findByStatusAndMyyja(Status.REQUESTED, user),
				ostoRepo.findByStatusAndOstaja(Status.REQUESTED, user)
				);
	}

	public List<Ostot> list(Status status) {
		return ostoRepo.findByStatus(status);
	}

	public void confirm(Long id, Members user) throws AuthorizationException {
		Ostot osto = ostoRepo.findOne(id);
		if(user != osto.getMyyja()) {
			throw new AuthorizationException();
		}
		else {
			//reject others
			List<Ostot> ostot = ostoRepo.findByKiekko(osto.getKiekko());
			ostot.remove(osto);
			if(ostot.size() > 0) {
				for(Ostot o : ostot) {
					o.setStatus(Status.REJECTED);
				}
				ostoRepo.save(ostot);
			}
			
			//change owner
			Kiekot kiekko = osto.getKiekko();
			kiekko.setMember(osto.getOstaja());
			kiekko.setMyynnissa(false);
			kiekkoRepo.save(kiekko);
			
			//set status to confirmed
			osto.setStatus(Status.CONFIRMED);
			ostoRepo.save(osto);
		}
	}
	
	public void reject(Long id, Members user) throws AuthorizationException {
		Ostot osto = ostoRepo.findOne(id);
		if(user != osto.getMyyja() && user != osto.getOstaja()) {
			throw new AuthorizationException();
		}
		else {
			osto.setStatus(Status.REJECTED);
			ostoRepo.save(osto);
		}
	}
}
