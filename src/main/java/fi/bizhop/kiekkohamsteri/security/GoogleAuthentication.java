package fi.bizhop.kiekkohamsteri.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthentication {
	private static final Logger LOG = LogManager.getLogger(GoogleAuthentication.class);

	private final GoogleIdTokenVerifier verifier;

	public GoogleAuthentication(VerifierProvider verifierProvider) {
		this.verifier = verifierProvider.getVerifier();
	}

	public String getUserEmail(String token) {
		LOG.debug(String.format("Authenticating with google token: %s", token));

		var email = verifyAndGetEmail(token);
		if(email != null) {
			LOG.debug(String.format("Google user found with email: %s", email));
		}
		return email;
	}

	private String verifyAndGetEmail(String token) {
		try {
			GoogleIdToken googleIdToken = verifier.verify(token);
			if(googleIdToken != null) {
				return googleIdToken.getPayload().getEmail();
			}
			else {
				LOG.error("Invalid ID token");
				return null;
			}
		} catch (Exception e) {
			LOG.error("Error handling id token", e);
			return null;
		}
	}
}
