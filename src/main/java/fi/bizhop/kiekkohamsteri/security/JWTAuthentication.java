package fi.bizhop.kiekkohamsteri.security;

import fi.bizhop.kiekkohamsteri.ShutdownManager;
import fi.bizhop.kiekkohamsteri.security.provider.JWTSecretKeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTAuthentication {
    private static final Logger LOG = LogManager.getLogger(JWTAuthentication.class);
    private static final long EXPIRATION_TIME = 1209600000L; //14 days
    public static final String JWT_TOKEN_PREFIX = "jwt";

    private final Key JWT_KEY;
    private final ShutdownManager shutdownManager;

    public JWTAuthentication(ShutdownManager shutdownManager, JWTSecretKeyProvider JWTSecretKeyProvider) {
        this.shutdownManager = shutdownManager;
        this.JWT_KEY = getJwtKey(JWTSecretKeyProvider.getKey());
    }

    //initialize JWT key or fail fast
    private Key getJwtKey(String base64) {
        try {
            var decoded = Decoders.BASE64.decode(base64);
            return Keys.hmacShaKeyFor(decoded);
        } catch (Exception e) {
            LOG.error("Error initializing JWT key", e);
            shutdownManager.shutdown();
        }
        return null;
    }

    public String getJwtForEmail(String email) {
        var claims = Jwts.claims();
        claims.put("email", email);

        var jwt = Jwts.builder()
            .setSubject(email)
            .setClaims(claims)
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(JWT_KEY)
            .compact();

        return String.format("%s%s", JWT_TOKEN_PREFIX, jwt);
    }

    public String getUserEmail(String token) {
        var claims = getClaims(token);

        if(claims == null || claims.get("email") == null) {
            return null;
        }

        return (String)claims.get("email");
    }

    private Claims getClaims(String token) {
        if(token == null) {
            return null;
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(JWT_KEY)
                    .build()
                .parseClaimsJws(token.replaceFirst(JWT_TOKEN_PREFIX,""))
                .getBody();
        } catch (RuntimeException e) {
            LOG.warn("Invalid jwt token: {}", token);
            return null;
        }
    }
}
