package fi.bizhop.kiekkohamsteri.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.bizhop.kiekkohamsteri.model.Members;

public interface MembersRepository extends CrudRepository<Members, Long> {
	Members findByEmail(String email);
	List<Members> findAllByOrderById();
}