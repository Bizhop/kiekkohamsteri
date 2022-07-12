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

	public Members updateDetails(Members user, UserUpdateDto dto, boolean adminRequest) {
		if(user == null) return null;

		var ignores = Utils.getNullPropertyNames(dto);
		if(!adminRequest) {
			ignores.add("level");
		}
		BeanUtils.copyProperties(dto, user, ignores.toArray(String[]::new));
		
		if(dto.isPublicList()) {
			userRepo.makeDiscsPublic(user);
		}
		
		return userRepo.save(user);
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
