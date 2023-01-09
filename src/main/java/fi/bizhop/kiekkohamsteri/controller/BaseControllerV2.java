package fi.bizhop.kiekkohamsteri.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(path="/api/v2")
public class BaseControllerV2 {
    protected static final Logger LOG = LogManager.getLogger(BaseControllerV2.class);
}
