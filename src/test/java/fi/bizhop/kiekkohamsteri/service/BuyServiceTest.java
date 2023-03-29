package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.TestObjects;
import fi.bizhop.kiekkohamsteri.db.BuyRepository;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.model.Buy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static fi.bizhop.kiekkohamsteri.model.Buy.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BuyServiceTest {
    @Mock
    BuyRepository buyRepository;

    @Captor
    ArgumentCaptor<Buy> buysCaptor;

    @Captor
    ArgumentCaptor<List<Buy>> buysListCaptor;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenNotOwnDiscAndIsMyynnissa_whenBuyDisc_thenReturnBuyWithRequestedStatus() throws HttpResponseException {
        var disc = TestObjects.getTestDiscFor(TEST_USER);
        disc.setForSale(true);

        when(buyRepository.save(any(Buy.class))).then(returnsFirstArg());

        var buy = getBuyService().buyDisc(OTHER_USER, disc);

        assertEquals(REQUESTED, buy.getStatus());
    }

    @Test
    void givenOwnDisc_whenBuyDisc_thenThrowException() {
        var disc = TestObjects.getTestDiscFor(TEST_USER);
        disc.setForSale(true);

        try {
            getBuyService().buyDisc(TEST_USER, disc);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (HttpResponseException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
            assertEquals("You can't buy your own disc", e.getMessage());
        }
    }

    @Test
    void givenDiscNotMyynnissa_whenBuyDisc_thenThrowException() {
        var disc = TestObjects.getTestDiscFor(TEST_USER);
        disc.setForSale(false);

        try {
            getBuyService().buyDisc(OTHER_USER, disc);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (HttpResponseException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getStatusCode());
            assertEquals("Not for sale", e.getMessage());
        }
    }

    @Test
    void givenAlreadyBuyingDisc_whenBuyDisc_thenThrowException() {
        var disc = TestObjects.getTestDiscFor(TEST_USER);
        disc.setForSale(true);

        var buy = new Buy(disc, TEST_USER, OTHER_USER, REQUESTED);

        when(buyRepository.findByDiscAndBuyerAndStatus(disc, OTHER_USER, REQUESTED))
                .thenReturn(buy);

        try {
            getBuyService().buyDisc(OTHER_USER, disc);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (HttpResponseException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
            assertEquals("You are already buying this disc", e.getMessage());
        }
    }

    @Test
    void getSummaryTest() {
        var sells = List.of(new Buy());
        var buys = List.of(new Buy(), new Buy());

        when(buyRepository.findByStatusAndSeller(REQUESTED, TEST_USER)).thenReturn(sells);
        when(buyRepository.findByStatusAndBuyer(REQUESTED, TEST_USER)).thenReturn(buys);

        var response = getBuyService().getSummary(TEST_USER);

        assertEquals(2, response.getAsBuyer().size());
        assertEquals(1, response.getAsSeller().size());
    }

    @Test
    void getListingWithStatusTest() {
        getBuyService().getListing(REQUESTED);

        verify(buyRepository, times(1)).findByStatus(REQUESTED);
        verify(buyRepository, never()).findAll();
    }

    @Test
    void getListingWithoutStatusTest() {
        getBuyService().getListing(null);

        verify(buyRepository, never()).findByStatus(any());
        verify(buyRepository, times(1)).findAll();
    }

    @Test
    void givenUserIsNotSeller_whenConfirm_thenThrowException() {
        var buy = new Buy(getTestDiscFor(TEST_USER), TEST_USER, OTHER_USER, REQUESTED);
        when(buyRepository.findById(123L)).thenReturn(Optional.of(buy));

        try {
            getBuyService().confirm(123L, OTHER_USER);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (AuthorizationException ignored) {}
    }

    @Test
    void givenBuyIsNotFound_whenConfirm_thenThrowException() {
        try {
            getBuyService().confirm(123L, TEST_USER);

            fail(SHOULD_THROW_EXCEPTION);
        } catch (Exception ignored) {}
    }

    @Test
    void givenValidRequest_whenConfirm_thenReturnModifiedDisc() throws AuthorizationException {
        var disc = getTestDiscFor(TEST_USER);
        disc.setForSale(true);
        disc.setItb(true);

        var buy = new Buy(disc, TEST_USER, OTHER_USER, REQUESTED);
        when(buyRepository.findById(123L)).thenReturn(Optional.of(buy));

        var response = getBuyService().confirm(123L, TEST_USER);

        assertEquals(OTHER_USER, response.getOwner());
        assertFalse(response.getForSale());
        assertFalse(response.getItb());
    }

    @Test
    void givenOtherRequestsExist_whenConfirm_thenClearOtherRequests() throws AuthorizationException {
        var disc = getTestDiscFor(TEST_USER);
        disc.setForSale(true);
        disc.setItb(true);

        var buy = new Buy(disc, TEST_USER, OTHER_USER, REQUESTED);
        buy.setId(1L);
        when(buyRepository.findById(1L)).thenReturn(Optional.of(buy));

        var fakeBuyer = new User("fake@example.com");
        var otherBuy = new Buy(disc, TEST_USER, fakeBuyer, REQUESTED);
        otherBuy.setId(2L);
        when(buyRepository.findByDisc(disc)).thenReturn(List.of(buy, otherBuy));

        var response = getBuyService().confirm(1L, TEST_USER);

        assertEquals(OTHER_USER, response.getOwner());
        assertFalse(response.getForSale());
        assertFalse(response.getItb());

        verify(buyRepository, times(1)).save(buysCaptor.capture());
        var savedBuy = buysCaptor.getValue();
        assertEquals(CONFIRMED, savedBuy.getStatus());

        verify(buyRepository, times(1)).saveAll(buysListCaptor.capture());
        var rejectedBuys = buysListCaptor.getValue();
        assertEquals(1, rejectedBuys.size());
        var rejectedBuy = rejectedBuys.get(0);
        assertEquals(fakeBuyer, rejectedBuy.getBuyer());
        assertEquals(REJECTED, rejectedBuy.getStatus());
    }

    @Test
    void givenUserIsSeller_whenReject_thenReject() throws AuthorizationException {
        var buy = new Buy(getTestDiscFor(TEST_USER), TEST_USER, OTHER_USER, REQUESTED);
        buy.setId(1L);

        when(buyRepository.findById(1L)).thenReturn(Optional.of(buy));

        getBuyService().reject(1L, TEST_USER);

        verify(buyRepository, times(1)).save(buysCaptor.capture());
        var savedBuy = buysCaptor.getValue();
        assertEquals(REJECTED, savedBuy.getStatus());
    }

    @Test
    void givenUserIsBuyer_whenReject_thenReject() throws AuthorizationException {
        var buy = new Buy(getTestDiscFor(TEST_USER), TEST_USER, OTHER_USER, REQUESTED);
        buy.setId(1L);

        when(buyRepository.findById(1L)).thenReturn(Optional.of(buy));

        getBuyService().reject(1L, OTHER_USER);

        verify(buyRepository, times(1)).save(buysCaptor.capture());
        var savedBuy = buysCaptor.getValue();
        assertEquals(REJECTED, savedBuy.getStatus());
    }

    @Test
    void givenUserIsNotSellerOrBuyer_whenReject_thenThrowException() {
        var buy = new Buy(getTestDiscFor(TEST_USER), TEST_USER, OTHER_USER, REQUESTED);
        buy.setId(1L);

        when(buyRepository.findById(1L)).thenReturn(Optional.of(buy));

        try {
            getBuyService().reject(1L, new User("fake@example.com"));

            fail(SHOULD_THROW_EXCEPTION);
        } catch (AuthorizationException ignored) {}
    }

    private BuyService getBuyService() {
        return new BuyService(buyRepository);
    }
}
