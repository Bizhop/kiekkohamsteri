package fi.bizhop.kiekkohamsteri.security;

import fi.bizhop.kiekkohamsteri.ShutdownManager;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class JWTAuthenticationTest {
    private static final String INVALID_KEY = "ABC";

    @Mock
    fi.bizhop.kiekkohamsteri.security.provider.JWTSecretKeyProvider JWTSecretKeyProvider;

    @Mock
    ShutdownManager shutdownManager;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenInvalidKey_thenFailFast() {
        given(JWTSecretKeyProvider.getKey()).willReturn(INVALID_KEY);

        getAuth();

        verify(shutdownManager, atLeastOnce()).shutdown();
    }

    @Test
    void givenNullKey_thenFailFast() {
        given(JWTSecretKeyProvider.getKey()).willReturn(null);

        getAuth();

        verify(shutdownManager, atLeastOnce()).shutdown();
    }

    @Test
    void givenValidKey_thenNotFailingFast() {
        provideValidKey();

        getAuth();

        verify(shutdownManager, never()).shutdown();
    }

    @Test
    void givenValidUser_whenAddAuthentication_thenAddJwt() {
        provideValidKey();

        var auth = getAuth();
        var jwt = auth.getJwtForEmail(TEST_EMAIL);

        assertNotNull(jwt);
        assertTrue(jwt.startsWith(JWTAuthentication.JWT_TOKEN_PREFIX));
    }

    @Test
    void givenValidToken_whenGetUserEmail_thenReturnEmailFromToken() {
        String key = getValidKey();
        given(JWTSecretKeyProvider.getKey()).willReturn(key);

        var token = getToken(TEST_EMAIL, key);
        var auth = getAuth();
        var userEmail = auth.getUserEmail(token);

        assertEquals(TEST_EMAIL, userEmail);
    }

    @Test
    void givenInvalidToken_whenGetUserEmail_thenReturnNull() {
        provideValidKey();

        var randomKey = getValidKey(); //not same key

        var token = getToken(TEST_EMAIL, randomKey);
        var auth = getAuth();
        var userEmail = auth.getUserEmail(token);

        assertNull(userEmail);
    }


    //HELPER METHODS

    private String getValidKey() {
        var secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        return Encoders.BASE64.encode(secretKey.getEncoded());
    }

    private JWTAuthentication getAuth() {
        return new JWTAuthentication(shutdownManager, JWTSecretKeyProvider);
    }

    private void provideValidKey() {
        given(JWTSecretKeyProvider.getKey()).willReturn(getValidKey());
    }

    private String getToken(String email, String key) {
        var claims = Jwts.claims();
        claims.put("email", email);

        var decodedKey = Decoders.BASE64.decode(key);
        var secretKey = Keys.hmacShaKeyFor(decodedKey);

        String jwt = Jwts.builder()
                .setSubject(email)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(secretKey)
                .compact();

        return String.format("%s%s", JWTAuthentication.JWT_TOKEN_PREFIX, jwt);
    }
}
