package fi.bizhop.kiekkohamsteri.db;

import fi.bizhop.kiekkohamsteri.model.Group;
import fi.bizhop.kiekkohamsteri.model.Role;
import fi.bizhop.kiekkohamsteri.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	User findByEmail(String email);
	List<User> findAllByOrderById();

	Integer countByCreatedAtBetween(Date beginDate, Date endDate);

	List<User> findAllByGroups(Group group);
	Page<User> findAllByGroups(Group group, Pageable pageable);

	List<User> findAllByRoles(Role role);
}