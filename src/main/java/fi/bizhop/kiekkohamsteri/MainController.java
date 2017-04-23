package fi.bizhop.kiekkohamsteri;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	@Autowired
	private VariRepository variRepo;
	
	@Autowired
	private KiekkoRepository kiekkoRepo;

	@GetMapping(path="/varit")
	public @ResponseBody Iterable<R_vari> haeVarit() {
		return variRepo.findAll();
	}
	
	@GetMapping(path="/kiekot")
	public @ResponseBody Iterable<Kiekot> haeKiekot() {
		return kiekkoRepo.findAll();
	}
	
	@RequestMapping(value="/kiekot/{id}", method=RequestMethod.GET)
	public @ResponseBody Kiekot haeKiekko(@PathVariable Long id) {
		return kiekkoRepo.findOne(id);
	}
}