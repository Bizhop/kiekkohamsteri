package fi.bizhop.kiekkohamsteri.security;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GoogleAuthentication {
	private static final String GOOGLE_PARSER_URL_TEMPLATE = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=%s";

	public static String getUserEmail(String token) throws Exception {
		Map<String, String> claims = getClaims(token);

		if (claims == null || claims.get("email") == null) {
			return null;
		}

		return claims.get("email");
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getClaims(String token) throws Exception {
		if (token == null) {
			return null;
		}

		try {
			String json = Jsoup.connect(String.format(GOOGLE_PARSER_URL_TEMPLATE, token)).ignoreContentType(true).execute().body();
			return new ObjectMapper().readValue(json, HashMap.class);
		}
		catch (Exception e) {
			return null;
		}
	}
}
