package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.ColorRepository;
import fi.bizhop.kiekkohamsteri.model.R_vari;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.TestObjects.COLORS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ColorServiceTest {
    @Mock
    ColorRepository colorRepo;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getColorTest() {
        var color = COLORS.get(0);

        when(colorRepo.findById(123L)).thenReturn(Optional.of(color));

        var response = getColorService().getColor(123L);

        assertTrue(response.isPresent());
        assertEquals(color, response.get());
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
