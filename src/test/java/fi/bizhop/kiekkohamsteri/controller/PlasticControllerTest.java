package fi.bizhop.kiekkohamsteri.controller;

import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.TestUtils;
import fi.bizhop.kiekkohamsteri.TestUtils.BaseAdder;
import fi.bizhop.kiekkohamsteri.dto.PlasticCreateDto;
import fi.bizhop.kiekkohamsteri.model.Members;
import fi.bizhop.kiekkohamsteri.model.R_muovi;
import fi.bizhop.kiekkohamsteri.model.R_valm;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.ManufacturerService;
import fi.bizhop.kiekkohamsteri.service.PlasticService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PlasticControllerTest extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;

    @MockBean AuthService authService;
    @MockBean PlasticService plasticService;
    @MockBean ManufacturerService manufacturerService;

    BaseAdder adder = new BaseAdder("expected/controller/plastic/");

    @Test
    void givenUnableToAuthenticateUser_whenCallingGetPlastics_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenNonAdminUser_whenCallingGetPlastics_thenRespondForbidden() {
        var user = new Members(TEST_EMAIL);
        user.setLevel(1);
        when(authService.getUser(any())).thenReturn(user);

        var response = restTemplate.getForEntity(createUrl(), Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenUnableToAuthenticateUser_whenCreatingPlastic_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.postForEntity(createUrl(), PlasticCreateDto.builder().build(), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenNonAdminUser_whenCreatingPlastic_thenRespondWithUnauthorized() {
        var user = new Members(TEST_EMAIL);
        user.setLevel(1);
        when(authService.getUser(any())).thenReturn(user);

        var response = restTemplate.postForEntity(createUrl(), PlasticCreateDto.builder().build(), Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenValidRequestWithoutManufacturerId_whenGetPlastics_thenReturnAllPlastics() throws IOException {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var page = new PageImpl<>(getPlastics());
        when(plasticService.getPlastics(any())).thenReturn(page);

        var response = restTemplate.getForEntity(createUrl(), String.class);

        verify(plasticService, times(1)).getPlastics(any());
        verify(manufacturerService, never()).getManufacturer(anyLong());
        verify(plasticService, never()).getPlasticsByManufacturer(any(), any());

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("allPlastics.json"), response.getBody());
    }

    @Test
    void givenValidRequestWithManufacturerId_whenGetPlastics_thenReturnPlasticsByManufacturer() throws IOException {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var manufacturer = MANUFACTURERS.get(0);
        var page = new PageImpl<>(getPlastics(manufacturer));
        when(manufacturerService.getManufacturer(0L)).thenReturn(Optional.of(manufacturer));
        when(plasticService.getPlasticsByManufacturer(eq(manufacturer), any())).thenReturn(page);

        var response = restTemplate.getForEntity(createUrl() + "?valmId=0", String.class);

        verify(plasticService, never()).getPlastics(any());
        verify(manufacturerService, times(1)).getManufacturer(0L);
        verify(plasticService, times(1)).getPlasticsByManufacturer(eq(manufacturer), any());

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("discmaniaPlastics.json"), response.getBody());
    }

    @Test
    void givenManufacturerNotFound_whenGetPlastics_thenRespondBadRequest() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        when(manufacturerService.getManufacturer(66L)).thenReturn(Optional.empty());

        var response = restTemplate.getForEntity(createUrl() + "?valmId=66", String.class);

        verify(plasticService, never()).getPlastics(any());
        verify(manufacturerService, times(1)).getManufacturer(66L);
        verify(plasticService, never()).getPlasticsByManufacturer(any(), any());

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenValidRequest_whenCreatePlastic_thenCreatePlastic() throws IOException {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var manufacturer = MANUFACTURERS.get(0);
        var dto = PlasticCreateDto.builder().valmId(0L).build();
        var mold = getTestPlastic(manufacturer);
        when(manufacturerService.getManufacturer(0L)).thenReturn(Optional.of(manufacturer));
        when(plasticService.createPlastic(dto, manufacturer)).thenReturn(projectionFromPlastic(mold));

        var response = restTemplate.postForEntity(createUrl(), dto, String.class);

        verify(manufacturerService, times(1)).getManufacturer(0L);
        verify(plasticService, times(1)).createPlastic(dto, manufacturer);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("newPlastic.json"), response.getBody());
    }

    @Test
    void givenManufacturerNotFound_whenCreatePlastic_thenRespondBadRequest() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var dto = PlasticCreateDto.builder().valmId(0L).build();
        when(manufacturerService.getManufacturer(0L)).thenReturn(Optional.empty());

        var response = restTemplate.postForEntity(createUrl(), dto, String.class);

        verify(manufacturerService, times(1)).getManufacturer(0L);
        verify(plasticService, never()).createPlastic(any(), any());

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }


    //HELPER METHODS

    private String createUrl() {
        return String.format("http://localhost:%d/api/muovit/", port);
    }

    private R_muovi getTestPlastic(R_valm manufacturer) {
        var plastic = new R_muovi();
        plastic.setId(66L);
        plastic.setValmistaja(manufacturer);
        plastic.setMuovi("New Plastic");
        return plastic;
    }
}
