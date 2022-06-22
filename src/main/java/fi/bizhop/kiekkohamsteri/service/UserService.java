package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.UserRepository;
import fi.bizhop.kiekkohamsteri.dto.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.v1.LeaderProjection;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
	private static final Logger LOG = LogManager.getLogger(UserService.class);
	
	final UserRepository userRepo;
	final DiscService discService;

	public List<Members> getUsers() {
		updateDiscCounts();
		return userRepo.findAllByOrderById();
	}

	public Members getUser(Long id) {
		return userRepo.findById(id).orElseThrow();
	}

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

	public Members setUserLevel(Long id, Integer level) {
		Members user = userRepo.findById(id).orElseThrow();
		
		user.setLevel(level);
		
		userRepo.save(user);
		return userRepo.findById(id).orElse(null);
	}

	public List<LeaderProjection> getLeaders() {
		List<Members> leaders = userRepo.findByPublicDiscCountTrue();
		for(Members leader : leaders) {
			if(leader.getDiscCount() == 0) {
				Integer count = discService.getDiscCountByUser(leader);
				leader.setDiscCount(count);
				userRepo.save(leader);
			}
		}
		return userRepo.findByPublicDiscCountTrueOrderByDiscCountDesc();
	}

	public Members saveUser(Members user) {
		return userRepo.save(user);
	}

	public List<Members> getUsersWithPublicList() {
		return userRepo.findByPublicListTrue();
	}

	private void updateDiscCounts() {
		var users = userRepo.findAll();
		for(var user : users) {
			var count = discService.getDiscCountByUser(user);
			user.setDiscCount(count);
			userRepo.save(user);
			LOG.debug(String.format("Updated member %d disc count to %d", user.getId(), count));
		}
	}
}
