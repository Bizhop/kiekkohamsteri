package fi.bizhop.kiekkohamsteri.db;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.Kiekot;

public interface KiekkoRepository extends CrudRepository<Kiekot, Long> {}