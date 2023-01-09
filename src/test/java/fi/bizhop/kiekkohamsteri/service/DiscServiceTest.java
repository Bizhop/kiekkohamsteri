package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.DiscRepository;
import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscInputDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscSearchDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.Disc;
import fi.bizhop.kiekkohamsteri.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class DiscServiceTest {
    @Mock
    DiscRepository discRepo;

    @Captor
    ArgumentCaptor<Disc> discCaptor;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenAnyUser_whenNewDisc_thenNewDiscIsNotPublic() {
        var user = new User(TEST_EMAIL);

        setupMocksForNewDisc();

        getDiscService().newDisc(user, MOLDS.get(0), PLASTICS.get(0), COLORS.get(0));

        verify(discRepo, times(1)).save(discCaptor.capture());

        var savedDisc = discCaptor.getValue();
        assertFalse(savedDisc.getPublicDisc());
    }

    @Test
    void givenUserIsDiscOwner_whenDeletingDisc_thenDeleteDisc() throws AuthorizationException {
        var disc = getTestDiscFor(TEST_USER);

        when(discRepo.findById(123L)).thenReturn(Optional.of(disc));

        getDiscService().deleteDisc(123L, TEST_USER);

        verify(discRepo, times(1)).deleteById(anyLong());
    }

    @Test
    void givenUserIsNotDiscOwner_whenDeletingDisc_thenThrowAuthException() {
        var disc = DISCS.stream()
                .filter(d -> OTHER_EMAIL.equals(d.getOwner().getEmail()))
                .findFirst();

        when(discRepo.findById(123L)).thenReturn(disc);

        try {
            getDiscService().deleteDisc(123L, TEST_USER);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (AuthorizationException ignored) {}

        verify(discRepo, never()).deleteById(anyLong());
    }

    @Test
    void givenUserIsNotDiscOwner_whenUpdatingDisc_thenThrowAuthException() {
        var disc = getTestDiscFor(OTHER_USER);

        when(discRepo.findById(123L)).thenReturn(Optional.of(disc));

        var dto = DiscInputDto.builder().build();

        try {
            getDiscService().updateDisc(dto, 123L, TEST_USER, null, null, null);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (AuthorizationException ignored) {}

        verify(discRepo, never()).save(any());
    }

    @Test
    void givenValidDto_whenUpdatingDisc_thenUpdateDisc() throws AuthorizationException {
        var disc = getTestDiscFor(TEST_USER);

        when(discRepo.findById(123L)).thenReturn(Optional.of(disc));

        var dto = DiscInputDto.builder()
                .description("text")
                .condition(8)
                .image("image")
                .build();

        getDiscService().updateDisc(dto, 123L, TEST_USER, null, null, null);

        verify(discRepo, times(1)).save(discCaptor.capture());

        var savedDisc = discCaptor.getValue();
        assertEquals("text", savedDisc.getDescription());
        assertEquals(8, savedDisc.getCondition());
        assertEquals("image",savedDisc.getImage());
    }

    @Test
    void givenValidDtoOnV2_whenUpdatingDisc_thenUpdateDisc() throws AuthorizationException {
        var disc = getTestDiscFor(TEST_USER);

        when(discRepo.findById(123L)).thenReturn(Optional.of(disc));

        var dto = fi.bizhop.kiekkohamsteri.dto.v2.in.DiscInputDto.builder()
                .description("text")
                .condition(8)
                .image("image")
                .build();

        getDiscService().updateDisc(dto, 123L, TEST_USER, null, null, null);

        verify(discRepo, times(1)).save(discCaptor.capture());

        var savedDisc = discCaptor.getValue();
        assertEquals("text", savedDisc.getDescription());
        assertEquals(8, savedDisc.getCondition());
        assertEquals("image",savedDisc.getImage());
    }

    @Test
    void givenLostIsTrue_whenUpdating_thenSetItbFalseAndForSaleFalse() throws AuthorizationException {
        var disc = getTestDiscFor(TEST_USER);
        disc.setItb(true);
        disc.setForSale(true);

        when(discRepo.findById(123L)).thenReturn(Optional.of(disc));

        var dto = DiscInputDto.builder().lost(true).build();

        getDiscService().updateDisc(dto, 123L, TEST_USER, null, null, null);

        verify(discRepo, times(1)).save(discCaptor.capture());

        var savedDisc = discCaptor.getValue();
        assertEquals(true, savedDisc.getLost());
        assertEquals(false, savedDisc.getItb());
        assertEquals(false, savedDisc.getForSale());
    }

    @Test
    void givenUserIsDiscOwner_whenGetDisc_thenReturnDisc() throws AuthorizationException {
        var disc = getDiscsByUserV2(TEST_USER).get(0);

        when(discRepo.findById(123L)).thenReturn(Optional.of(disc));

        var response = getDiscService().getDisc(TEST_USER, 123L);

        assertEquals(disc, response);
    }

    @Test
    void givenUserIsNotDiscOwner_whenGetDisc_thenThrowAuthException() {
        var disc = getDiscsByUserV2(OTHER_USER).get(0);

        when(discRepo.findById(anyLong())).thenReturn(Optional.of(disc));

        try {
            getDiscService().getDisc(TEST_USER, 0L);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (AuthorizationException ignored) {}
    }

    @Test
    void givenDiscIsPublic_whenGetOtherUsersDisc_thenReturnDisc() throws AuthorizationException, HttpResponseException {
        var disc = getTestDiscFor(OTHER_USER);
        disc.setPublicDisc(true);

        when(discRepo.findById(123L)).thenReturn(Optional.of(disc));

        var response = getDiscService().getDiscIfPublicOrOwnV2(TEST_USER, 123L);

        assertEquals(disc, response);
    }

    @Test
    void givenDiscIsNotPublic_whenGetOtherUsersDisc_thenThrowAuthException() {
        var disc = getTestDiscFor(OTHER_USER);
        disc.setPublicDisc(false);

        when(discRepo.findById(123L)).thenReturn(Optional.of(disc));

        try {
            getDiscService().getDiscIfPublicOrOwnV2(TEST_USER, 123L);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (HttpResponseException hre) {
            fail(WRONG_EXCEPTION);
        } catch (AuthorizationException ignored) {}
    }

    @Test
    void givenDiscNotFound_whenGetOtherUsersDisc_thenThrowNotFoundException() {
        when(discRepo.findById(123L)).thenReturn(Optional.empty());

        try {
            getDiscService().getDiscIfPublicOrOwnV2(TEST_USER, 123L);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (AuthorizationException | HttpResponseException e) {
            fail(WRONG_EXCEPTION);
        } catch (NoSuchElementException ignored) {}
    }

    @Test
    void givenCriteriaIsNull_whenSearch_thenThrowBadRequestException() {
        try {
            getDiscService().search(null, null, DiscSearchDto.builder().build());
        } catch (HttpResponseException hre) {
            assertEquals(SC_BAD_REQUEST, hre.getStatusCode());
        }
    }

    DiscService getDiscService() {
        return new DiscService(discRepo);
    }

    void setupMocksForNewDisc() {
        when(discRepo.save(any(Disc.class))).then(returnsFirstArg());
    }
}
