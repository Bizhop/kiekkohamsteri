package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class DropdownsServiceTest {
    @Mock
    MoldRepository moldRepo;

    @Mock
    ManufacturerRepository manufacturerRepo;

    @Mock
    PlasticRepository plasticRepo;

    @Mock
    ColorRepository colorRepo;

    @Mock
    DropdownRepository ddRepo;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDropdownsTest() {
        when(moldRepo.findAllByOrderByNameAsc()).thenReturn(MOLDS);
        when(manufacturerRepo.findAll()).thenReturn(MANUFACTURERS);
        when(plasticRepo.findAllByOrderByNameAsc()).thenReturn(PLASTICS);
        when(colorRepo.findAll()).thenReturn(COLORS);
        when(ddRepo.findByMenuOrderByValueAsc("kunto")).thenReturn(CONDITIONS);
        when(ddRepo.findByMenuOrderByValueAsc("tussit")).thenReturn(MARKINGS);

        var dto = getDropdownsService().getDropdowns(null);

        assertEquals(6, dto.getMolds().size());
        assertEquals(5, dto.getPlastics().size());
        assertEquals(4, dto.getConditions().size());
        assertEquals(3, dto.getManufacturers().size());
        assertEquals(2, dto.getColors().size());
        assertEquals(1, dto.getMarkings().size());
    }

    @Test
    void getDropdownsByManufacturerTest() {
        var manufacturer = MANUFACTURERS.get(0);

        when(manufacturerRepo.findAll()).thenReturn(MANUFACTURERS);
        when(manufacturerRepo.findById(manufacturer.getId())).thenReturn(Optional.of(MANUFACTURERS.get(0)));
        when(colorRepo.findAll()).thenReturn(COLORS);
        when(ddRepo.findByMenuOrderByValueAsc("kunto")).thenReturn(CONDITIONS);
        when(ddRepo.findByMenuOrderByValueAsc("tussit")).thenReturn(MARKINGS);

        when(moldRepo.findByManufacturerOrderByNameAsc(manufacturer)).thenReturn(getMolds(manufacturer));
        when(plasticRepo.findByManufacturerOrderByNameAsc(manufacturer)).thenReturn(getPlastics(manufacturer));

        var dto = getDropdownsService().getDropdowns(manufacturer.getId());

        assertEquals(2, dto.getMolds().size());
        assertEquals(2, dto.getPlastics().size());
        assertEquals(4, dto.getConditions().size());
        assertEquals(3, dto.getManufacturers().size());
        assertEquals(2, dto.getColors().size());
        assertEquals(1, dto.getMarkings().size());
    }

    DropdownsService getDropdownsService() {
        return new DropdownsService(moldRepo, manufacturerRepo, plasticRepo, colorRepo, ddRepo);
    }
}