package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.PlasticRepository;
import fi.bizhop.kiekkohamsteri.dto.MoldCreateDto;
import fi.bizhop.kiekkohamsteri.dto.PlasticCreateDto;
import fi.bizhop.kiekkohamsteri.model.R_mold;
import fi.bizhop.kiekkohamsteri.model.R_muovi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.TestObjects.MANUFACTURERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

public class PlasticServiceTest {
    @Mock
    PlasticRepository plasticRepository;

    @Captor
    ArgumentCaptor<R_muovi> plasticCaptor;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDefaultPlasticTest() {
        var defaultPlastic = new R_muovi();
        defaultPlastic.setId(13L);
        defaultPlastic.setMuovi("ANY");

        when(plasticRepository.findById(13L)).thenReturn(Optional.of(defaultPlastic));

        var response = getPlasticService().getDefaultPlastic();

        verify(plasticRepository, times(1)).findById(13L);
        assertEquals(defaultPlastic, response);
    }

    @Test
    void createPlasticTest() {
        var manufacturer = MANUFACTURERS.get(0);

        var dto = PlasticCreateDto.builder()
                .valmId(manufacturer.getId())
                .muovi("TEST")
                .build();

        when(plasticRepository.save(any(R_muovi.class))).then(returnsFirstArg());

        getPlasticService().createPlastic(dto, manufacturer);

        verify(plasticRepository, times(1)).save(plasticCaptor.capture());

        var saved = plasticCaptor.getValue();

        assertEquals("TEST", saved.getMuovi());
        assertEquals(manufacturer.getId(), saved.getValmistaja().getId());
    }

    private PlasticService getPlasticService() {
        return new PlasticService(plasticRepository);
    }
}
