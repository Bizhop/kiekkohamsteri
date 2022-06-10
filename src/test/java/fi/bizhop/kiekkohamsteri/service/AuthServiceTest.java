package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.MembersRepository;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.security.GoogleAuthentication;
import fi.bizhop.kiekkohamsteri.security.JWTAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_EMAIL;
import static fi.bizhop.kiekkohamsteri.security.JWTAuthentication.JWT_TOKEN_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    @Mock
    MembersRepository membersRepository;

    @Mock
    JWTAuthentication jwtAuthentication;

    @Mock
    GoogleAuthentication googleAuthentication;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidJwt_whenGetUser_thenReturnUser() {
        var testUser = new Members(TEST_EMAIL);
        given(jwtAuthentication.getUserEmail(anyString())).willReturn(TEST_EMAIL);
        given(membersRepository.findByEmail(anyString())).willReturn(testUser);

        var token = String.format("%s%s", JWT_TOKEN_PREFIX, "anything-goes");
        var request = mock(HttpServletRequest.class);
        given(request.getHeader(anyString())).willReturn(token);

        var user = getAuth().getUser(request);

        assertNotNull(user);
        assertEquals(TEST_EMAIL, user.getEmail());
    }

    @Test
    void givenValidGoogleToken_whenGetUser_thenReturnUser() {
        var testUser = new Members(TEST_EMAIL);
        given(googleAuthentication.getUserEmail(anyString())).willReturn(TEST_EMAIL);
        given(membersRepository.findByEmail(anyString())).willReturn(testUser);

        var token = "anything-goes";
        var request = mock(HttpServletRequest.class);
        given(request.getHeader(anyString())).willReturn(token);

        var user = getAuth().getUser(request);

        assertNotNull(user);
        assertEquals(TEST_EMAIL, user.getEmail());
    }

    @Test
    void givenNoValidToken_whenGetUser_thenReturnNull() {
        var token = "anything-goes";
        var request = mock(HttpServletRequest.class);
        given(request.getHeader(anyString())).willReturn(token);

        var user = getAuth().getUser(request);

        assertNull(user);
    }

    @Test
    void givenValidTokenAndUserDoesntExist_whenGetUser_thenReturnNull() {
        given(jwtAuthentication.getUserEmail(anyString())).willReturn(TEST_EMAIL);
        given(membersRepository.findByEmail(anyString())).willReturn(null);

        var token = String.format("%s%s", JWT_TOKEN_PREFIX, "anything-goes");
        var request = mock(HttpServletRequest.class);
        given(request.getHeader(anyString())).willReturn(token);

        var user = getAuth().getUser(request);

        assertNull(user);
    }

    @Test
    void givenValidToken_whenLogin_thenReturnUser() {
        var testUser = new Members(TEST_EMAIL);
        given(jwtAuthentication.getUserEmail(anyString())).willReturn(TEST_EMAIL);
        given(membersRepository.findByEmail(anyString())).willReturn(testUser);

        var token = String.format("%s%s", JWT_TOKEN_PREFIX, "anything-goes");
        var request = mock(HttpServletRequest.class);
        given(request.getHeader(anyString())).willReturn(token);

        var user = getAuth().login(request);

        assertNotNull(user);
        assertEquals(TEST_EMAIL, user.getEmail());
        verify(membersRepository, never()).save(any());
    }

    @Test
    void givenValidTokenAndUserDoesntExist_whenLogin_thenSaveAndReturnUser() {
        given(jwtAuthentication.getUserEmail(anyString())).willReturn(TEST_EMAIL);
        given(membersRepository.findByEmail(anyString())).willReturn(null);
        given(membersRepository.save(any(Members.class))).willAnswer(i -> {
            var user = (Members)i.getArguments()[0];
            if(user.getId() == null) user.setId(1L);
            return user;
        });

        var token = String.format("%s%s", JWT_TOKEN_PREFIX, "anything-goes");
        var request = mock(HttpServletRequest.class);
        given(request.getHeader(anyString())).willReturn(token);

        var user = getAuth().login(request);

        assertNotNull(user);
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals("Uusi1", user.getUsername());
        verify(membersRepository, times(2)).save(any());
    }

    //HELPER METHODS
    AuthService getAuth() {
        return new AuthService(membersRepository, jwtAuthentication, googleAuthentication);
    }
}
