package fi.bizhop.kiekkohamsteri.controller;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.v1.in.PlasticCreateDto;
import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import fi.bizhop.kiekkohamsteri.model.Plastic;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.ManufacturerService;
import fi.bizhop.kiekkohamsteri.service.PlasticService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.stream.Collectors;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.CONTROLLER;
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

    @Captor ArgumentCaptor<Pageable> pageableCaptor;

    BaseAdder adder = new BaseAdder("plastic", CONTROLLER);

    @Test
    void givenUnableToAuthenticateUser_whenCallingGetPlastics_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenNonAdminUser_whenCallingGetPlastics_thenRespondForbidden() {
        var user = new User(TEST_EMAIL);
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
        var user = new User(TEST_EMAIL);
        user.setLevel(1);
        when(authService.getUser(any())).thenReturn(user);

        var response = restTemplate.postForEntity(createUrl(), PlasticCreateDto.builder().build(), Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenValidRequestWithoutManufacturerId_whenGetPlastics_thenReturnAllPlastics() {
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
    void givenValidRequestWithManufacturerId_whenGetPlastics_thenReturnPlasticsByManufacturer() {
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
    void givenValidRequest_whenCreatePlastic_thenCreatePlastic() {
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

    @Test
    void v1CompatibilityTest() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        restTemplate.getForEntity(createUrl("?size=1000&sort=muovi,asc"), String.class);

        verify(plasticService, times(1)).getPlastics(pageableCaptor.capture());

        var sorts = pageableCaptor.getValue().getSort().get().collect(Collectors.toList());
        assertEqualsJson(adder.create("compatibilitySorts.json"), sorts);
    }


    //HELPER METHODS

    private String createUrl() {
        return createUrl("");
    }

    private String createUrl(String params) {
        return String.format("http://localhost:%d/api/muovit/%s", port, params);
    }

    private Plastic getTestPlastic(Manufacturer manufacturer) {
        var plastic = new Plastic();
        plastic.setId(66L);
        plastic.setManufacturer(manufacturer);
        plastic.setName("New Plastic");
        return plastic;
    }
}
