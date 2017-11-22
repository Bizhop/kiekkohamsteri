package fi.bizhop.kiekkohamsteri.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.MembersRepository;
import fi.bizhop.kiekkohamsteri.dto.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.util.Utils;

@Service
public class MemberService {
	@Autowired
	MembersRepository membersRepo;

	public List<Members> getUsers() {
		return membersRepo.findAllByOrderById();
	}

	public Members getUser(Long id) {
		return membersRepo.findOne(id);
	}

	public Members updateDetails(Long id, UserUpdateDto dto) {
		Members user = membersRepo.findOne(id);
		
		String[] ignoreNulls = Utils.getNullPropertyNames(dto);
		BeanUtils.copyProperties(dto, user, ignoreNulls);
		
		membersRepo.save(user);
		return membersRepo.findOne(id);
	}

	public Members setUserLevel(Long id, Integer level) {
		Members user = membersRepo.findOne(id);
		
		user.setLevel(level);
		
		membersRepo.save(user);
		return membersRepo.findOne(id);
	}

}
