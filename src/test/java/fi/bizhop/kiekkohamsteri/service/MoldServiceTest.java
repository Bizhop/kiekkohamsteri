package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.dto.MoldCreateDto;
import fi.bizhop.kiekkohamsteri.model.R_mold;
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

public class MoldServiceTest {
    @Mock
    MoldRepository moldRepo;

    @Captor
    ArgumentCaptor<R_mold> moldCaptor;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDefaultMoldTest() {
        var defaultMold = new R_mold();
        defaultMold.setId(893L);
        defaultMold.setKiekko("ANY");

        when(moldRepo.findById(893L)).thenReturn(Optional.of(defaultMold));

        var response = getMoldService().getDefaultMold();

        verify(moldRepo, times(1)).findById(893L);
        assertEquals(defaultMold, response);
    }

    @Test
    void createMoldTest() {
        var manufacturer = MANUFACTURERS.get(0);

        var dto = MoldCreateDto.builder()
                .valmId(manufacturer.getId())
                .kiekko("TEST")
                .build();

        when(moldRepo.save(any(R_mold.class))).then(returnsFirstArg());

        getMoldService().createMold(dto, manufacturer);

        verify(moldRepo, times(1)).save(moldCaptor.capture());

        var saved = moldCaptor.getValue();
        assertEquals("TEST", saved.getKiekko());
        assertEquals(manufacturer.getId(), saved.getValmistaja().getId());
    }

    private MoldService getMoldService() {
        return new MoldService(moldRepo);
    }
}
