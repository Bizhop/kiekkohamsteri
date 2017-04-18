package fi.bizhop.kiekkohamsteri;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fi.bizhop.kiekkohamsteri.model.R_vari;
import fi.bizhop.kiekkohamsteri.db.VariRepository;

@Controller
@RequestMapping(path="/hamsteri")
public class MainController {
	@Autowired
	private VariRepository variRepo;

	@GetMapping(path="/varit")
	public @ResponseBody Iterable<R_vari> haeVarit() {
		return variRepo.findAll();
	}
}
