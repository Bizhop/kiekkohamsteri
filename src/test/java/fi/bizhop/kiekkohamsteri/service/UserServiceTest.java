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
        var dto = UserUpdateDto.builder()
                .etunimi("TEST")
                .sukunimi("USER")
                .publicList(false)
                .build();

        getUserService().updateDetails(TEST_USER, dto);

        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(userRepository, never()).makeDiscsPublic(any());

        var saved = userCaptor.getValue();
        assertEquals("TEST", saved.getEtunimi());
        assertEquals("USER", saved.getSukunimi());
        assertFalse(saved.getPublicList());
    }

    @Test
    void givenPublicListTrue_whenUpdateDetails_thenUpdateDetailsAndMakeDiscsPublic() {
        var dto = UserUpdateDto.builder()
                .etunimi("TEST")
                .sukunimi("USER")
                .publicList(true)
                .build();

        getUserService().updateDetails(TEST_USER, dto);

        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(userRepository, times(1)).makeDiscsPublic(TEST_USER);

        var saved = userCaptor.getValue();
        assertEquals("TEST", saved.getEtunimi());
        assertEquals("USER", saved.getSukunimi());
        assertTrue(saved.getPublicList());
    }

    private UserService getUserService() {
        return new UserService(userRepository);
    }
}
