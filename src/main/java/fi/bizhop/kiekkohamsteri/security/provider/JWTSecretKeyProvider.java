package fi.bizhop.kiekkohamsteri.security.provider;

import org.springframework.stereotype.Component;

@Component
public class JWTSecretKeyProvider {
    public String getKey() {
        return System.getenv("HAMSTERI_JWT_SECRET");
    }
}
