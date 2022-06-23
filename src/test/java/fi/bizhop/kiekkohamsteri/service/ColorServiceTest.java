package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.ColorRepository;
import fi.bizhop.kiekkohamsteri.model.R_vari;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ColorServiceTest {
    @Mock
    ColorRepository colorRepo;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDefaultColorTest() {
        var defaultColor = new R_vari();
        defaultColor.setId(1L);
        defaultColor.setVari("ANY");

        when(colorRepo.findById(1L)).thenReturn(Optional.of(defaultColor));

        var response = getColorService().getDefaultColor();

        verify(colorRepo, times(1)).findById(1L);
        assertEquals(response, defaultColor);
    }

    private ColorService getColorService() {
        return new ColorService(colorRepo);
    }
}
