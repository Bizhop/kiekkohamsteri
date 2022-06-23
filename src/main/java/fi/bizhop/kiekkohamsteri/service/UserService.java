package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.UserRepository;
import fi.bizhop.kiekkohamsteri.dto.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.v1.LeaderProjection;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
	final UserRepository userRepo;

	public Members updateDetails(Long id, UserUpdateDto dto) {
		Members user = userRepo.findById(id).orElseThrow();
		
		String[] ignoreNulls = Utils.getNullPropertyNames(dto);
		BeanUtils.copyProperties(dto, user, ignoreNulls);
		
		if(dto.isPublicList()) {
			userRepo.makeDiscsPublic(user);
		}
		
		userRepo.save(user);
		return userRepo.findById(id).orElse(null);
	}

	// Passthrough methods to db
	// Not covered (or to be covered by unit tests)

	@Transactional
	public Members setUserLevel(Long id, Integer level) {
		userRepo.updateUserLevel(id, level);
		return userRepo.findById(id).orElse(null);
	}

	public Members getUser(Long id) {
		return userRepo.findById(id).orElseThrow();
	}

	public List<Members> getUsers() {
		return userRepo.findAllByOrderById();
	}

	public void saveUser(Members user) {
		userRepo.save(user);
	}

	public void saveUsers(List<Members> users) {
		userRepo.saveAll(users);
	}

	public List<LeaderProjection> getLeaders() {
		return userRepo.findByPublicDiscCountTrueOrderByDiscCountDesc();
	}

	public List<Members> getUsersWithPublicList() {
		return userRepo.findByPublicListTrue();
	}
}