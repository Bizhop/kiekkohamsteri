package fi.bizhop.kiekkohamsteri.security;

import fi.bizhop.kiekkohamsteri.model.Members;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.log4j.Logger;

import java.util.Date;

public class JWTAuthentication {
    private static final Logger LOG = Logger.getLogger(JWTAuthentication.class);
    private static final long EXPIRATION_TIME = 1209600000l; //14 days
    private static final String JWT_SECRET = System.getenv("HAMSTERI_JWT_SECRET");
    public static final String JWT_TOKEN_PREFIX = "jwt";

    public static void addAuthentication(Members member) {
        String email = member.getEmail();
        Claims claims = Jwts.claims();
        claims.put("email", email);

        String jwt = Jwts.builder()
            .setSubject(email)
            .setClaims(claims)
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
            .compact();

        member.setJwt(JWT_TOKEN_PREFIX + jwt);
        LOG.debug("Added jwt for " + email);
    }

    public static String getUserEmail(String token) {
        Claims claims = getClaims(token);

        if(claims == null || claims.get("email") == null) {
            return null;
        }

        String email = (String)claims.get("email");
        return email;
    }

    private static Claims getClaims(String token) {
        if(token == null) {
            return null;
        }

        try {
            return Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token.replace(JWT_TOKEN_PREFIX,""))
                .getBody();
        } catch (RuntimeException e) {
            return null;
        }
    }
}
