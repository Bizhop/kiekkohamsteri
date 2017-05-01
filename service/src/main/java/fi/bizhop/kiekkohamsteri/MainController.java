package fi.bizhop.kiekkohamsteri;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fi.bizhop.kiekkohamsteri.db.KiekkoRepository;
import fi.bizhop.kiekkohamsteri.db.VariRepository;
import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.R_vari;

@Controller
@RequestMapping(path="/hamsteri")
public class MainController {
	private static final Logger LOG = Logger.getLogger(MainController.class);
	
	@Autowired
	private VariRepository variRepo;
	
	@Autowired
	private KiekkoRepository kiekkoRepo;

	@GetMapping(path="/varit")
	public @ResponseBody Iterable<R_vari> haeVarit() {
		LOG.debug("MainController.haeVarit()...");
		return variRepo.findAll();
	}
	
	@GetMapping(path="/kiekot")
	public @ResponseBody Iterable<Kiekot> haeKiekot() {
		LOG.debug("MainController.haeKiekot()...");
		return kiekkoRepo.findAll();
	}
	
	@GetMapping(value="/kiekot/{id}")
	public @ResponseBody Kiekot haeKiekko(@PathVariable Long id) {
		LOG.debug(String.format("MainController.haeKiekko(%d)...", id));
		return kiekkoRepo.findOne(id);
	}
	
	@PostMapping(value="/kiekot")
	public @ResponseBody Kiekot lisaaKiekko() {
		LOG.debug("MainController.lisaaKiekko()...");
		
		Kiekot kiekko = new Kiekot();
		kiekko = kiekkoRepo.save(kiekko);
		
		LOG.debug(String.format("Lisätty kiekko, id=%d", kiekko.getId()));
		
		return kiekko;
	}
	
	@PutMapping(value="/kiekot/{id}")
	public @ResponseBody Kiekot muutaKiekkoa(@RequestBody Kiekot kiekko) {
		LOG.debug(String.format("MainController.muutaKiekkoa()... id=%d", kiekko.getId()));
		
		kiekko = kiekkoRepo.save(kiekko);
		
		LOG.debug(String.format("Muutettu kiekkoa, id=%s", kiekko.getId()));
		
		return kiekko;
	}
	
	@DeleteMapping(value="/kiekot/{id}")
	public @ResponseBody boolean poistaKiekko(@PathVariable Long id) {
		LOG.debug(String.format("MainController.poistaKiekko(%d)...", id));
		
		if(kiekkoRepo.exists(id)) {
			kiekkoRepo.delete(id);
			return true;
		}
		else {
			return false;
		}
	}
}