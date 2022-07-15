package fi.bizhop.kiekkohamsteri.service;

import java.time.LocalDate;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.DiscRepository;
import fi.bizhop.kiekkohamsteri.db.UserRepository;
import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.db.PlasticRepository;
import fi.bizhop.kiekkohamsteri.db.BuyRepository;
import fi.bizhop.kiekkohamsteri.db.StatsRepository;
import fi.bizhop.kiekkohamsteri.db.ManufacturerRepository;
import fi.bizhop.kiekkohamsteri.model.Ostot.Status;
import fi.bizhop.kiekkohamsteri.model.Stats;
import fi.bizhop.kiekkohamsteri.util.Utils;

import static fi.bizhop.kiekkohamsteri.model.Ostot.Status.CONFIRMED;
import static fi.bizhop.kiekkohamsteri.service.StatsService.UpdateStatus.DONE;
import static fi.bizhop.kiekkohamsteri.service.StatsService.UpdateStatus.FAILED;

@Service
@RequiredArgsConstructor
public class StatsService {
	private static final Logger LOG = LogManager.getLogger(StatsService.class);

	final StatsRepository statsRepo;
	final DiscRepository discRepo;
	final UserRepository userRepo;
	final ManufacturerRepository manufacturerRepo;
	final PlasticRepository plasticRepo;
	final MoldRepository moldRepo;
	final BuyRepository buyRepo;

	public UpdateStatus generateStatsByYearAndMonth(int year, int month) {
		if(year < 2017 || month < 1 || month > 12) {
			return FAILED;
		}

		try {
			LocalDate begin = LocalDate.of(year, month, 1);
			LocalDate end = begin.plusMonths(1).withDayOfMonth(1);

			Stats stats = statsRepo.findByYearAndMonth(year, month);
			if (stats == null) {
				stats = new Stats(year, month);
			}

			Date beginDate = Utils.asDate(begin);
			Date endDate = Utils.asDate(end);

			stats.setNewDiscs(discRepo.countByCreatedAtBetween(beginDate, endDate));
			stats.setNewUsers(userRepo.countByCreatedAtBetween(beginDate, endDate));
			stats.setNewManufacturers(manufacturerRepo.countByCreatedAtBetween(beginDate, endDate));
			stats.setNewPlastics(plasticRepo.countByCreatedAtBetween(beginDate, endDate));
			stats.setNewMolds(moldRepo.countByCreatedAtBetween(beginDate, endDate));
			stats.setSalesCompleted(buyRepo.countByUpdatedAtBetweenAndStatus(beginDate, endDate, CONFIRMED));

			statsRepo.save(stats);
			return DONE;
		}
		catch (Exception e) {
			LOG.error("Stats generation failed", e);
			return FAILED;
		}
	}

	public Page<Stats> getStats(Pageable pageable) {
		return statsRepo.findAll(pageable);
	}

	public enum UpdateStatus {
		DONE("Scheduled update done"), FAILED("Scheduled update failed");
		private final String stringValue;

		UpdateStatus(String stringValue) {
			this.stringValue = stringValue;
		}

		@Override
		public String toString() { return this.stringValue; }
	}
}
