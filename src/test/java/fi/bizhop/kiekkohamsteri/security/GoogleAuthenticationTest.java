package fi.bizhop.kiekkohamsteri.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import fi.bizhop.kiekkohamsteri.security.provider.GoogleIdTokenVerifierProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class GoogleAuthenticationTest {
    @Mock
    GoogleIdTokenVerifierProvider googleIdTokenVerifierProvider;

    @Mock
    GoogleIdTokenVerifier googleIdTokenVerifier;

    @Mock
    GoogleIdToken googleIdToken;

    @Mock
    GoogleIdToken.Payload payload;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidGoogleToken_whenGetUserEmail_thenReturnUserEmail() throws GeneralSecurityException, IOException {
        given(googleIdTokenVerifierProvider.getVerifier()).willReturn(googleIdTokenVerifier);
        given(googleIdTokenVerifier.verify(anyString())).willReturn(googleIdToken);
        given(googleIdToken.getPayload()).willReturn(payload);
        given(payload.getEmail()).willReturn(TEST_EMAIL);

        var email = getAuth().getUserEmail("valid :)"); //input token doesn't matter in this test

        assertEquals(email, TEST_EMAIL);
    }

    @Test
    void givenInvalidToken_whenGetUserEmail_thenReturnNull() throws GeneralSecurityException, IOException {
        given(googleIdTokenVerifierProvider.getVerifier()).willReturn(googleIdTokenVerifier);
        given(googleIdTokenVerifier.verify(anyString())).willReturn(null);

        var email = getAuth().getUserEmail("invalid :("); //input token doesn't matter in this test

        assertNull(email);
    }

    @Test
    void givenInvalidResponseFromGoogle_whenGetUserEmail_thenReturnNull() throws GeneralSecurityException, IOException {
        given(googleIdTokenVerifierProvider.getVerifier()).willReturn(googleIdTokenVerifier);
        given(googleIdTokenVerifier.verify(anyString())).willReturn(googleIdToken);
        //null payload will produce NPE, that should get caught in the method
        given(googleIdToken.getPayload()).willReturn(null);

        var email = getAuth().getUserEmail("any");

        assertNull(email);

    }

    //HELPER METHODS
    private GoogleAuthentication getAuth() {
        return new GoogleAuthentication(googleIdTokenVerifierProvider);
    }
}
