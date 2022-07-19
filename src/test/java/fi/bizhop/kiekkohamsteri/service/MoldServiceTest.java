package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.MoldRepository;
import fi.bizhop.kiekkohamsteri.dto.v1.in.MoldCreateDto;
import fi.bizhop.kiekkohamsteri.model.Mold;
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
    ArgumentCaptor<Mold> moldCaptor;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDefaultMoldTest() {
        var defaultMold = new Mold();
        defaultMold.setId(893L);
        defaultMold.setName("ANY");

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

        when(moldRepo.save(any(Mold.class))).then(returnsFirstArg());

        getMoldService().createMold(dto, manufacturer);

        verify(moldRepo, times(1)).save(moldCaptor.capture());

        var saved = moldCaptor.getValue();
        assertEquals("TEST", saved.getName());
        assertEquals(manufacturer.getId(), saved.getManufacturer().getId());
    }

    private MoldService getMoldService() {
        return new MoldService(moldRepo);
    }
}
