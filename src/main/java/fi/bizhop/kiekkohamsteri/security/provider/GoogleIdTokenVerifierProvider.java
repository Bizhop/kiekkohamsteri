package fi.bizhop.kiekkohamsteri.security.provider;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GoogleIdTokenVerifierProvider {
    public GoogleIdTokenVerifier getVerifier() {
        var audiencesEnv = System.getenv("HAMSTERI_GOOGLE_AUDIENCES");
        var audiences = Arrays.stream(audiencesEnv.split(","))
                .map(prefix -> prefix + ".apps.googleusercontent.com")
                .collect(Collectors.toList());

        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(audiences)
                .build();
    }
}
