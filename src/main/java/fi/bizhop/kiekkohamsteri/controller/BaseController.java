package fi.bizhop.kiekkohamsteri.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(path="/api")
public class BaseController {
	protected static final Logger LOG = Logger.getLogger(BaseController.class);
}