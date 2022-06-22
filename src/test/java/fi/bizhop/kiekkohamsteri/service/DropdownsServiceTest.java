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
        when(moldRepo.findAllByOrderByKiekkoAsc()).thenReturn(getMolds());
        when(manufacturerRepo.findAllProjectedBy()).thenReturn(getManufacturers());
        when(plasticRepo.findAllByOrderByMuoviAsc()).thenReturn(getPlastics());
        when(colorRepo.findAllProjectedBy()).thenReturn(getColors());
        when(ddRepo.findByValikkoOrderByArvoAsc("kunto")).thenReturn(getConditions());
        when(ddRepo.findByValikkoOrderByArvoAsc("tussit")).thenReturn(getMarkings());

        var dto = getDropdownsService().getDropdowns(null);

        assertEquals(6, dto.getMolds().size());
        assertEquals(5, dto.getMuovit().size());
        assertEquals(4, dto.getKunto().size());
        assertEquals(3, dto.getValms().size());
        assertEquals(2, dto.getVarit().size());
        assertEquals(1, dto.getTussit().size());
    }

    @Test
    void getDropdownsByManufacturerTest() {
        var manufacturer = MANUFACTURERS.get(0);

        when(manufacturerRepo.findAllProjectedBy()).thenReturn(getManufacturers());
        when(manufacturerRepo.findById(manufacturer.getId())).thenReturn(Optional.of(MANUFACTURERS.get(0)));
        when(colorRepo.findAllProjectedBy()).thenReturn(getColors());
        when(ddRepo.findByValikkoOrderByArvoAsc("kunto")).thenReturn(getConditions());
        when(ddRepo.findByValikkoOrderByArvoAsc("tussit")).thenReturn(getMarkings());

        when(moldRepo.findByValmistajaOrderByKiekkoAsc(manufacturer)).thenReturn(getMolds(manufacturer));
        when(plasticRepo.findByValmistajaOrderByMuoviAsc(manufacturer)).thenReturn(getPlastics(manufacturer));

        var dto = getDropdownsService().getDropdowns(manufacturer.getId());

        assertEquals(2, dto.getMolds().size());
        assertEquals(2, dto.getMuovit().size());
        assertEquals(4, dto.getKunto().size());
        assertEquals(3, dto.getValms().size());
        assertEquals(2, dto.getVarit().size());
        assertEquals(1, dto.getTussit().size());
    }

    DropdownsService getDropdownsService() {
        return new DropdownsService(moldRepo, manufacturerRepo, plasticRepo, colorRepo, ddRepo);
    }
}