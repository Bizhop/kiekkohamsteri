package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.TestObjects;
import fi.bizhop.kiekkohamsteri.db.DiscRepository;
import fi.bizhop.kiekkohamsteri.dto.DiscDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.Kiekot;
import fi.bizhop.kiekkohamsteri.model.Members;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class DiscServiceTest {
    @Mock
    DiscRepository discRepo;

    @Captor
    ArgumentCaptor<Kiekot> discCaptor;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenNewDisc_whenUserHasPublicListTrue_thenNewDiscIsPublic() {
        var user = new Members(TEST_EMAIL);
        user.setPublicList(true);

        setupMocksForNewDisc();

        getDiscService().newDisc(user, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0));

        verify(discRepo, times(1)).save(discCaptor.capture());

        var savedDisc = discCaptor.getValue();
        assertTrue(savedDisc.getPublicDisc());
    }

    @Test
    void givenNewDisc_whenUserHasPublicListFalse_thenNewDiscIsNotPublic() {
        var user = new Members(TEST_EMAIL);
        user.setPublicList(false);

        setupMocksForNewDisc();

        getDiscService().newDisc(user, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0));

        verify(discRepo, times(1)).save(discCaptor.capture());

        var savedDisc = discCaptor.getValue();
        assertFalse(savedDisc.getPublicDisc());
    }

    @Test
    void updateImageTest() {
        var disc = getTestDiscFor(TEST_USER);
        var id = 123L;
        disc.setId(id);
        when(discRepo.findById(id)).thenReturn(Optional.of(disc));
        when(discRepo.save(any(Kiekot.class))).then(returnsFirstArg());

        getDiscService().updateImage(id, "new-image");

        verify(discRepo, times(1)).save(any());
        assertEquals("new-image", disc.getKuva());
    }

    @Test
    void givenUserIsDiscOwner_whenDeletingDisc_thenDeleteDisc() throws AuthorizationException {
        var disc = getTestDiscFor(TEST_USER);

        when(discRepo.findById(123L)).thenReturn(Optional.of(disc));

        getDiscService().deleteDisc(123L, TEST_USER);

        verify(discRepo, times(1)).deleteById(anyLong());
    }

    @Test
    void givenUserIsNotDiscOwner_whenDeletingDisc_thenThrowException() {
        var disc = DISCS.stream()
                .filter(d -> OTHER_EMAIL.equals(d.getMember().getEmail()))
                .findFirst();

        when(discRepo.findById(123L)).thenReturn(disc);

        try {
            getDiscService().deleteDisc(123L, TEST_USER);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (AuthorizationException ignored) {}

        verify(discRepo, never()).deleteById(anyLong());
    }

    @Test
    void givenUserIsNotDiscOwner_whenUpdatingDisc_thenThrowException() throws HttpResponseException {
        var disc = getTestDiscFor(OTHER_USER);

        when(discRepo.findById(123L)).thenReturn(Optional.of(disc));

        var dto = DiscDto.builder().build();

        try {
            getDiscService().updateDisc(dto, 123L, TEST_USER, null, null, null);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (AuthorizationException ignored) {}

        verify(discRepo, never()).save(any());
    }

    @Test
    void givenValidDto_whenUpdatingDisc_thenUpdateDisc() throws AuthorizationException, HttpResponseException {
        var disc = getTestDiscFor(TEST_USER);

        when(discRepo.findById(123L)).thenReturn(Optional.of(disc));

        var dto = DiscDto.builder()
                .muuta("text")
                .kunto(8)
                .kuva("image")
                .build();

        getDiscService().updateDisc(dto, 123L, TEST_USER, null, null, null);

        verify(discRepo, times(1)).save(discCaptor.capture());

        var savedDisc = discCaptor.getValue();
        assertEquals("text", savedDisc.getMuuta());
        assertEquals(8, savedDisc.getKunto());
        assertEquals("image",savedDisc.getKuva());
    }

    @Test
    void givenDtoLostIsTrue_whenUpdating_thenSetItbFalseAndMyynnissaFalse() throws AuthorizationException, HttpResponseException {
        var disc = getTestDiscFor(TEST_USER);
        disc.setItb(true);
        disc.setMyynnissa(true);

        when(discRepo.findById(123L)).thenReturn(Optional.of(disc));

        var dto = DiscDto.builder().lost(true).build();

        getDiscService().updateDisc(dto, 123L, TEST_USER, null, null, null);

        verify(discRepo, times(1)).save(discCaptor.capture());

        var savedDisc = discCaptor.getValue();
        assertEquals(true, savedDisc.getLost());
        assertEquals(false, savedDisc.getItb());
        assertEquals(false, savedDisc.getMyynnissa());
    }

    @Test
    void givenUserIsDiscOwner_whenGetDisc_thenReturnDisc() throws AuthorizationException {
        var disc = getDiscsByUser(TEST_USER).get(0);

        when(discRepo.getKiekotById(123L)).thenReturn(disc);

        var response = getDiscService().getDisc(TEST_USER, 123L);

        assertEquals(disc, response);
    }

    @Test
    void givenUserIsNotDiscOwner_whenGetDisc_thenThrowException() {
        var disc = getDiscsByUser(OTHER_USER).get(0);

        when(discRepo.getKiekotById(anyLong())).thenReturn(disc);

        try {
            getDiscService().getDisc(TEST_USER, 0L);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (AuthorizationException ignored) {}
    }

    @Test
    void givenDiscIsPublic_whenGetOtherUsersDisc_thenReturnDisc() throws AuthorizationException {
        var disc = getTestDiscFor(OTHER_USER);
        disc.setPublicDisc(true);
        var projection = projectionFromDisc(disc);

        when(discRepo.getKiekotById(123L)).thenReturn(projection);

        var response = getDiscService().getDiscIfPublicOrOwn(TEST_USER, 123L);

        assertEquals(projection, response);
    }

    @Test
    void givenDiscIsNotPublic_whenGetOtherUsersDisc_thenThrowException() {
        var disc = getTestDiscFor(OTHER_USER);
        disc.setPublicDisc(false);
        var projection = projectionFromDisc(disc);

        when(discRepo.getKiekotById(123L)).thenReturn(projection);

        try {
            getDiscService().getDiscIfPublicOrOwn(TEST_USER, 123L);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (AuthorizationException ignored) {}
    }

    @Test
    void publicListsTest() {
        var discs = DISCS.stream()
                .filter(Kiekot::getPublicDisc)
                .map(TestObjects::projectionFromDisc)
                .collect(Collectors.toList());

        var users = List.of(TEST_USER, OTHER_USER);
        when(discRepo.findByMemberInAndPublicDiscTrue(users)).thenReturn(discs);

        var publicLists = getDiscService().getPublicLists(users);

        assertEquals(2, publicLists.size());
        var testUserListing = publicLists.stream()
                .filter(listing -> TEST_USER.getUsername().equals(listing.getUsername()))
                .findFirst().orElse(null);
        var otherUserListing = publicLists.stream()
                .filter(listing -> OTHER_USER.getUsername().equals(listing.getUsername()))
                .findFirst().orElse(null);

        assertNotNull(testUserListing);
        assertEquals(2, testUserListing.getKiekot().size());
        assertNotNull(otherUserListing);
        assertEquals(1, otherUserListing.getKiekot().size());
    }

    DiscService getDiscService() {
        return new DiscService(discRepo);
    }

    void setupMocksForNewDisc() {
        when(discRepo.save(any(Kiekot.class))).then(returnsFirstArg());
    }
}
