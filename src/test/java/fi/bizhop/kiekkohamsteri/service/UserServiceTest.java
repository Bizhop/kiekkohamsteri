package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.UserRepository;
import fi.bizhop.kiekkohamsteri.dto.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.Members;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_EMAIL;
import static fi.bizhop.kiekkohamsteri.TestObjects.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Captor
    ArgumentCaptor<Members> userCaptor;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenPublicListFalse_whenUpdateDetails_thenUpdateDetails() {
        var user = new Members(TEST_EMAIL);

        var dto = UserUpdateDto.builder()
                .etunimi("TEST")
                .sukunimi("USER")
                .publicList(false)
                .build();

        getUserService().updateDetails(user, dto, false);

        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(userRepository, never()).makeDiscsPublic(any());

        var saved = userCaptor.getValue();
        assertEquals("TEST", saved.getEtunimi());
        assertEquals("USER", saved.getSukunimi());
        assertFalse(saved.getPublicList());
    }

    @Test
    void givenPublicListTrue_whenUpdateDetails_thenUpdateDetailsAndMakeDiscsPublic() {
        var user = new Members(TEST_EMAIL);

        var dto = UserUpdateDto.builder()
                .etunimi("TEST")
                .sukunimi("USER")
                .publicList(true)
                .build();

        getUserService().updateDetails(user, dto, false);

        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(userRepository, times(1)).makeDiscsPublic(user);

        var saved = userCaptor.getValue();
        assertEquals("TEST", saved.getEtunimi());
        assertEquals("USER", saved.getSukunimi());
        assertTrue(saved.getPublicList());
    }

    @Test
    void givenAdminRequest_whenUpdateDetailsWithLevel_thenUpdateDetailsWithLevel() {
        var user = new Members(TEST_EMAIL);

        var dto = UserUpdateDto.builder().level(2).build();

        getUserService().updateDetails(user, dto, true);

        verify(userRepository, times(1)).save(userCaptor.capture());

        var saved = userCaptor.getValue();
        assertEquals(2, saved.getLevel());
    }

    @Test
    void givenNonAdminRequest_whenUpdateDetailsWithLevel_thenDontUpdateLevel() {
        var user = new Members(TEST_EMAIL);

        var dto = UserUpdateDto.builder().level(2).build();

        getUserService().updateDetails(user, dto, false);

        verify(userRepository, times(1)).save(userCaptor.capture());

        var saved = userCaptor.getValue();
        assertEquals(1, saved.getLevel());
    }

    private UserService getUserService() {
        return new UserService(userRepository);
    }
}
