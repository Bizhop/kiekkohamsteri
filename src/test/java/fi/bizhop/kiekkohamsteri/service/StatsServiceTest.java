package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.*;
import fi.bizhop.kiekkohamsteri.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static fi.bizhop.kiekkohamsteri.model.Ostot.Status.CONFIRMED;
import static fi.bizhop.kiekkohamsteri.service.StatsService.UpdateStatus.DONE;
import static fi.bizhop.kiekkohamsteri.service.StatsService.UpdateStatus.FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class StatsServiceTest {
    @Mock
    StatsRepository statsRepo;

    @Mock
    DiscRepository discRepo;

    @Mock
    UserRepository userRepo;

    @Mock
    ManufacturerRepository manufacturerRepo;

    @Mock
    PlasticRepository plasticRepo;

    @Mock
    MoldRepository moldRepo;

    @Mock
    BuyRepository buyRepo;

    @Captor
    ArgumentCaptor<Stats> statsCaptor;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateStatsTest() {
        when(discRepo.countByCreatedAtBetween(any(Date.class), any(Date.class))).thenReturn(1);
        when(userRepo.countByCreatedAtBetween(any(Date.class), any(Date.class))).thenReturn(2);
        when(manufacturerRepo.countByCreatedAtBetween(any(Date.class), any(Date.class))).thenReturn(3);
        when(plasticRepo.countByCreatedAtBetween(any(Date.class), any(Date.class))).thenReturn(4);
        when(moldRepo.countByCreatedAtBetween(any(Date.class), any(Date.class))).thenReturn(5);
        when(buyRepo.countByUpdatedAtBetweenAndStatus(any(Date.class), any(Date.class), eq(CONFIRMED))).thenReturn(6);

        var response = getStatsService().generateStatsByYearAndMonth(2022,6);

        verify(statsRepo, times(1)).save(statsCaptor.capture());

        assertEquals(DONE, response);

        var saved = statsCaptor.getValue();

        assertEquals(2022, saved.getYear());
        assertEquals(6, saved.getMonth());

        assertEquals(1, saved.getNewDiscs());
        assertEquals(2, saved.getNewUsers());
        assertEquals(3, saved.getNewManufacturers());
        assertEquals(4, saved.getNewPlastics());
        assertEquals(5, saved.getNewMolds());
        assertEquals(6, saved.getSalesCompleted());
    }

    @Test
    void givenBadYear_whenGenerateStats_thenReturnFailed() {
        var response = getStatsService().generateStatsByYearAndMonth(2015, 8);
        assertEquals(FAILED, response);
    }

    @Test
    void givenBadMonth_whenGenerateStats_thenReturnFailed() {
        var response1 = getStatsService().generateStatsByYearAndMonth(2022, 0);
        assertEquals(FAILED, response1);

        var response2 = getStatsService().generateStatsByYearAndMonth(2022, 33);
        assertEquals(FAILED, response2);
    }

    @Test
    void givenDbCallThrowsException_whenGenerateStats_thenReturnFailed() {
        when(statsRepo.findByYearAndMonth(2022, 5)).thenThrow(new RuntimeException("random stuff"));

        var response = getStatsService().generateStatsByYearAndMonth(2022, 5);
        assertEquals(FAILED, response);
    }

    private StatsService getStatsService() {
        return new StatsService(statsRepo, discRepo, userRepo, manufacturerRepo, plasticRepo, moldRepo, buyRepo);
    }
}
