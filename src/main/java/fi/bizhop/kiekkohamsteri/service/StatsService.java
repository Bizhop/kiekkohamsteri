package fi.bizhop.kiekkohamsteri.service;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.KiekkoRepository;
import fi.bizhop.kiekkohamsteri.db.MembersRepository;
import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.db.MuoviRepository;
import fi.bizhop.kiekkohamsteri.db.OstoRepository;
import fi.bizhop.kiekkohamsteri.db.StatsRepository;
import fi.bizhop.kiekkohamsteri.db.ValmRepository;
import fi.bizhop.kiekkohamsteri.model.Ostot.Status;
import fi.bizhop.kiekkohamsteri.model.Stats;
import fi.bizhop.kiekkohamsteri.util.Utils;

@Service
public class StatsService {
	@Autowired
	StatsRepository statsRepo;
	@Autowired
	KiekkoRepository kiekkoRepo;
	@Autowired
	MembersRepository membersRepo;
	@Autowired
	ValmRepository valmRepo;
	@Autowired
	MuoviRepository muoviRepo;
	@Autowired
	MoldRepository moldRepo;
	@Autowired
	OstoRepository ostoRepo;

	public boolean generateStats() {
		//last month
		LocalDate monthBegin = LocalDate.now().minusMonths(1).withDayOfMonth(1);
		LocalDate monthEnd = LocalDate.now().withDayOfMonth(1);
		return generateStats(monthBegin, monthEnd);
	}

	public Page<Stats> getStats(Pageable pageable) {
		return statsRepo.findAll(pageable);
	}

	public boolean generateStatsByYearAndMonth(int year, int month) {
		if(year < 2017 || month < 1 || month > 12) {
			return false;
		}
		LocalDate begin = LocalDate.of(year, month, 1);
		LocalDate end = begin.plusMonths(1).withDayOfMonth(1);
		return generateStats(begin, end);
	}
	
	private boolean generateStats(LocalDate begin, LocalDate end) {
		int year = begin.getYear();
		int month = begin.getMonthValue();
		
		Stats stats = statsRepo.findByYearAndMonth(year, month);
		if(stats == null) {
			stats = new Stats(year, month);
		}
		
		Date beginDate = Utils.asDate(begin);
		Date endDate = Utils.asDate(end);
				
		stats.setNewDiscs(kiekkoRepo.countByCreatedAtBetween(beginDate, endDate));
		stats.setNewUsers(membersRepo.countByCreatedAtBetween(beginDate, endDate));
		stats.setNewManufacturers(valmRepo.countByCreatedAtBetween(beginDate, endDate));
		stats.setNewPlastics(muoviRepo.countByCreatedAtBetween(beginDate, endDate));
		stats.setNewMolds(moldRepo.countByCreatedAtBetween(beginDate, endDate));
		stats.setSalesCompleted(ostoRepo.countByUpdatedAtBetweenAndStatus(beginDate, endDate, Status.CONFIRMED));
		
		statsRepo.save(stats);
		
		return true;
	}
}
