package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.UserRepository;
import fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.User;
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

	//V1 compatibility
	public User updateDetails(User user, fi.bizhop.kiekkohamsteri.dto.v1.in.UserUpdateDto dto, boolean adminRequest) {
		var dtoV2 = UserUpdateDto.fromV1(dto);
		return updateDetails(user, dtoV2, adminRequest);
	}

	public User updateDetails(User user, UserUpdateDto dto, boolean adminRequest) {
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

	public User getUser(Long id) {
		return userRepo.findById(id).orElseThrow();
	}

	public List<User> getUsers() {
		return userRepo.findAllByOrderById();
	}

	public void saveUser(User user) {
		userRepo.save(user);
	}

	public void saveUsers(List<User> users) {
		userRepo.saveAll(users);
	}

	public List<LeaderProjection> getLeaders() {
		return userRepo.findByPublicDiscCountTrueOrderByDiscCountDesc();
	}

	public List<User> getUsersWithPublicList() {
		return userRepo.findByPublicListTrue();
	}
}
