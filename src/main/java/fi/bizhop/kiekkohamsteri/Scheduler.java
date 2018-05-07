package fi.bizhop.kiekkohamsteri;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fi.bizhop.kiekkohamsteri.service.StatsService;

@Component
public class Scheduler {
	@Autowired
	StatsService statsService;
	
	private static final Logger LOG = Logger.getLogger(Scheduler.class);
	
	@Scheduled(cron="0 0 5 * * *")
	public void updateStats() {
		LOG.info("Scheduler updating stats...");
		
		if(statsService.generateStats()) {
			LOG.info("Scheduled update done");
		}
		else {
			LOG.error("Scheduled update failed");
		}
	}
}
