package fi.bizhop.kiekkohamsteri;

import java.time.LocalDate;

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
	
	@Scheduled(cron="0 0 5 1 * *")
	public void updateLastMonthStats() {
		LOG.info("Scheduler updating last month stats...");
		
		if(statsService.generateStats()) {
			LOG.info("Scheduled update done");
		}
		else {
			LOG.error("Scheduled update failed");
		}
	}
	
	@Scheduled(cron="0 0 6 * * *")
	public void updateCurrentMonthStats() {
		LocalDate now = LocalDate.now();
		int year = now.getYear();
		int month = now.getMonthValue();
		
		LOG.info(String.format("Scheduler updating current month stats (%d-%d)...", month, year));
		
		if(statsService.generateStatsByYearAndMonth(year, month)) {
			LOG.info("Scheduled update done");
		}
		else {
			LOG.error("Scheduled update failed");
		}
	}

}
