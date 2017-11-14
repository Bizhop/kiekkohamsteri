package fi.bizhop.kiekkohamsteri.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.MembersRepository;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.security.TokenAuthentication;

@Service
public class AuthService {
	@Autowired
	MembersRepository membersRepo;
	
	public Members getUser(HttpServletRequest request) {
		try {
			String userEmail = TokenAuthentication.getUserEmail(request);
			return membersRepo.findByEmail(userEmail);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Members getUser(String email) {
		return membersRepo.findByEmail(email);
	}

	public Members login(HttpServletRequest request) throws Exception {
		String userEmail = TokenAuthentication.getUserEmail(request);
		if(userEmail == null) {
			return null;
		}
		else {
			Members user = membersRepo.findByEmail(userEmail);
			if(user == null) {
				user = new Members(userEmail);
				user = membersRepo.save(user);
				user.setUsername("Uusi" + user.getId());
				user = membersRepo.save(user);
			}
			return user;
		}
	}
}