package fi.bizhop.kiekkohamsteri.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VerifierProvider {
    public GoogleIdTokenVerifier getVerifier() {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(List.of(System.getenv("HAMSTERI_GOOGLE_CLIENT_ID")))
                .build();
    }
}
