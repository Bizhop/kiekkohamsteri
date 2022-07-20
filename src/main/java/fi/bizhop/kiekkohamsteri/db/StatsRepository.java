package fi.bizhop.kiekkohamsteri.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fi.bizhop.kiekkohamsteri.model.Stats;

import javax.annotation.Nonnull;

public interface StatsRepository extends PagingAndSortingRepository<Stats, Integer> {
	@Override
	@Nonnull
	Page<Stats> findAll(@Nonnull Pageable pageable);

	Stats findByYearAndMonth(int year, int month);
}
