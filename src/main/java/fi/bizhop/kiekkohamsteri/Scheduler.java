package fi.bizhop.kiekkohamsteri;

import java.time.LocalDate;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fi.bizhop.kiekkohamsteri.service.StatsService;

@Component
public class Scheduler {
	@Autowired
	StatsService statsService;
	
	private static final Logger LOG = LogManager.getLogger(Scheduler.class);
	
	@Scheduled(cron="0 0 5 * * *")
	public void updateStats() {
		LocalDate now = LocalDate.now();
		int year = now.getYear();
		int month = now.getMonthValue();
		if(now.getDayOfMonth() == 1) {
			month--;
		}
		
		LOG.info(String.format("Scheduler updating stats (%d-%d)...", month, year));
		LOG.info(statsService.generateStatsByYearAndMonth(year,month));
	}
}
