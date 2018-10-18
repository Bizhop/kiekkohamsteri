package fi.bizhop.kiekkohamsteri.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.bizhop.kiekkohamsteri.security.JWTAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.bizhop.kiekkohamsteri.db.MembersRepository;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.security.GoogleAuthentication;

@Service
public class AuthService {
    private static final String HEADER_STRING = "Authorization";
	@Autowired
	MembersRepository membersRepo;
	
	public Members getUser(HttpServletRequest request) {
		try {
            return membersRepo.findByEmail(getEmail(request));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Members login(HttpServletRequest request) throws Exception {
		String userEmail = getEmail(request);
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
            JWTAuthentication.addAuthentication(user);
			return user;
		}
	}

	private String getEmail(HttpServletRequest request) throws Exception {
        String token = request.getHeader(HEADER_STRING);
        String userEmail = null;
        if(token.startsWith(JWTAuthentication.JWT_TOKEN_PREFIX)) {
            userEmail = JWTAuthentication.getUserEmail(token);
        }
        if(userEmail == null) {
            userEmail = GoogleAuthentication.getUserEmail(token);
        }
        return userEmail;
    }
}