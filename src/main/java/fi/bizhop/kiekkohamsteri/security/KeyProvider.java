package fi.bizhop.kiekkohamsteri.security;

import org.springframework.stereotype.Component;

@Component
public class KeyProvider {
    public String getKey() {
        return System.getenv("HAMSTERI_JWT_SECRET");
    }
}
