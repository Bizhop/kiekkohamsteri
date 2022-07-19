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
        when(moldRepo.findAllByOrderByNameAsc()).thenReturn(getMoldsDD());
        when(manufacturerRepo.findAllProjectedBy()).thenReturn(getManufacturersDD());
        when(plasticRepo.findAllByOrderByNameAsc()).thenReturn(getPlasticsDD());
        when(colorRepo.findAllProjectedBy()).thenReturn(getColorsDD());
        when(ddRepo.findByMenuOrderByValueAsc("kunto")).thenReturn(getConditionsDD());
        when(ddRepo.findByMenuOrderByValueAsc("tussit")).thenReturn(getMarkingsDD());

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

        when(manufacturerRepo.findAllProjectedBy()).thenReturn(getManufacturersDD());
        when(manufacturerRepo.findById(manufacturer.getId())).thenReturn(Optional.of(MANUFACTURERS.get(0)));
        when(colorRepo.findAllProjectedBy()).thenReturn(getColorsDD());
        when(ddRepo.findByMenuOrderByValueAsc("kunto")).thenReturn(getConditionsDD());
        when(ddRepo.findByMenuOrderByValueAsc("tussit")).thenReturn(getMarkingsDD());

        when(moldRepo.findByManufacturerOrderByNameAsc(manufacturer)).thenReturn(getMoldsDD(manufacturer));
        when(plasticRepo.findByManufacturerOrderByNameAsc(manufacturer)).thenReturn(getPlasticsDD(manufacturer));

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