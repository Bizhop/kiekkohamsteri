package fi.bizhop.kiekkohamsteri.controller.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController extends BaseController {
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String ping() {
		return "pong";
	}
}