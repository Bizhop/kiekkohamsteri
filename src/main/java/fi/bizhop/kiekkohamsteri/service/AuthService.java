package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.UserRepository;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.security.GoogleAuthentication;
import fi.bizhop.kiekkohamsteri.security.JWTAuthentication;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AuthService {
	private static final Logger LOG = LogManager.getLogger(AuthService.class);
    private static final String HEADER_STRING = "Authorization";

	private final UserRepository membersRepo;
	private final JWTAuthentication jwtAuthentication;
	private final GoogleAuthentication googleAuthentication;
	
	public Members getUser(HttpServletRequest request) {
		var userEmail = getEmail(request);
		if(userEmail == null) return null;

		try {
			return membersRepo.findByEmail(userEmail);
		} catch (Exception e) {
			LOG.error("Error getting user: {}", userEmail);
			return null;
		}
	}
	
	public Members login(HttpServletRequest request) {
		var userEmail = getEmail(request);
		if(userEmail == null) return null;

		var user = membersRepo.findByEmail(userEmail);
		if(user == null) {
			user = new Members(userEmail);
			user = membersRepo.save(user);
			user.setUsername(String.format("Uusi%d", user.getId()));
			user = membersRepo.save(user);
		}
		var jwt = jwtAuthentication.getJwtForEmail(userEmail);
		user.setJwt(jwt);
		return user;
	}

	private String getEmail(HttpServletRequest request) {
        var token = request.getHeader(HEADER_STRING);
		String userEmail = null;
        if(token.startsWith(JWTAuthentication.JWT_TOKEN_PREFIX)) {
            userEmail = jwtAuthentication.getUserEmail(token);
        }
        if(userEmail == null) {
            userEmail = googleAuthentication.getUserEmail(token);
        }
        return userEmail;
    }
}