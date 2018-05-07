package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.Stats;

public interface StatsRepository extends CrudRepository<Stats, Integer> {
	List<Stats> findAll();
	Stats findByYearAndMonth(int year, int month);
}
