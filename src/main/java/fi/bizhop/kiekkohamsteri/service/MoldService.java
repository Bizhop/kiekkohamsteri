package fi.bizhop.kiekkohamsteri.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.projection.MoldProjection;

@Service
public class MoldService {
	@Autowired
	MoldRepository moldRepo;
	
	public List<MoldProjection> getMolds() {
		return moldRepo.findAllProjectedBy();
	}
}
