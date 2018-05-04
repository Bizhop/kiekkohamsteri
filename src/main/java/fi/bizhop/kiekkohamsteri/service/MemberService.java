package fi.bizhop.kiekkohamsteri.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.KiekkoRepository;
import fi.bizhop.kiekkohamsteri.db.MembersRepository;
import fi.bizhop.kiekkohamsteri.dto.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.projection.LeaderProjection;
import fi.bizhop.kiekkohamsteri.util.Utils;

@Service
public class MemberService {
	private static final Logger LOG = Logger.getLogger(MemberService.class);
	
	@Autowired
	MembersRepository membersRepo;
	@Autowired
	KiekkoRepository kiekkoRepo;

	public List<Members> getUsers() {
		updateDiscCounts();
		return membersRepo.findAllByOrderById();
	}

	public Members getUser(Long id) {
		return membersRepo.findOne(id);
	}

	public Members updateDetails(Long id, UserUpdateDto dto) {
		Members user = membersRepo.findOne(id);
		
		String[] ignoreNulls = Utils.getNullPropertyNames(dto);
		BeanUtils.copyProperties(dto, user, ignoreNulls);
		
		if(dto.getPublicList()) {
			membersRepo.makeDiscsPublic(user);
		}
		
		membersRepo.save(user);
		return membersRepo.findOne(id);
	}

	public Members setUserLevel(Long id, Integer level) {
		Members user = membersRepo.findOne(id);
		
		user.setLevel(level);
		
		membersRepo.save(user);
		return membersRepo.findOne(id);
	}

	public List<LeaderProjection> getLeaders() {
		List<Members> leaders = membersRepo.findByPublicDiscCountTrue();
		for(Members l : leaders) {
			if(l.getDiscCount() == 0) {
				l.setDiscCount(kiekkoRepo.findByMember(l).size());
				membersRepo.save(l);
			}
		}
		return membersRepo.findByPublicDiscCountTrueOrderByDiscCountDesc();
	}

	private void updateDiscCounts() {
		Iterable<Members> members = membersRepo.findAll();
		for(Members m : members) {
			List<Kiekot> kiekot = kiekkoRepo.findByMember(m);
			m.setDiscCount(kiekot.size());
			membersRepo.save(m);
			LOG.debug(String.format("Updated member %d disc count to %d", m.getId(), kiekot.size()));
		}
	}
}
