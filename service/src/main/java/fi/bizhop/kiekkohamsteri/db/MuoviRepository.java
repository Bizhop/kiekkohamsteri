package fi.bizhop.kiekkohamsteri.db;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.R_muovi;

public interface MuoviRepository extends CrudRepository<R_muovi, Long> {}