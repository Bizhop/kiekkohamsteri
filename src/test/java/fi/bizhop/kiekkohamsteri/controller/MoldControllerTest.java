package fi.bizhop.kiekkohamsteri.controller;

import fi.bizhop.kiekkohamsteri.BaseAdder;
import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.v1.in.MoldCreateDto;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.model.Mold;
import fi.bizhop.kiekkohamsteri.model.Manufacturer;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.ManufacturerService;
import fi.bizhop.kiekkohamsteri.service.MoldService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.BaseAdder.Type.CONTROLLER;
import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MoldControllerTest extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;

    @MockBean AuthService authService;
    @MockBean MoldService moldService;
    @MockBean ManufacturerService manufacturerService;

    BaseAdder adder = new BaseAdder("mold", CONTROLLER);

    @Test
    void givenUnableToAuthenticateUser_whenCallingGetMolds_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenNonAdminUser_whenCallingGetMolds_thenRespondForbidden() {
        var user = new User(TEST_EMAIL);
        user.setLevel(1);
        when(authService.getUser(any())).thenReturn(user);

        var response = restTemplate.getForEntity(createUrl(), Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenUnableToAuthenticateUser_whenCreatingMold_thenRespondWithUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.postForEntity(createUrl(), MoldCreateDto.builder().build(), Object.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenNonAdminUser_whenCreatingMold_thenRespondWithUnauthorized() {
        var user = new User(TEST_EMAIL);
        user.setLevel(1);
        when(authService.getUser(any())).thenReturn(user);

        var response = restTemplate.postForEntity(createUrl(), MoldCreateDto.builder().build(), Object.class);

        assertEquals(SC_FORBIDDEN, response.getStatusCodeValue());
    }

    @Test
    void givenValidRequestWithoutManufacturerId_whenGetMolds_thenReturnAllMolds() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var page = new PageImpl<>(getMolds());
        when(moldService.getMolds(any())).thenReturn(page);

        var response = restTemplate.getForEntity(createUrl(), String.class);

        verify(moldService, times(1)).getMolds(any());
        verify(manufacturerService, never()).getManufacturer(anyLong());
        verify(moldService, never()).getMoldsByManufacturer(any(), any());

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("allMolds.json"), response.getBody());
    }

    @Test
    void givenValidRequestWithManufacturerId_whenGetMolds_thenReturnMoldsByManufacturer() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var manufacturer = MANUFACTURERS.get(0);
        var page = new PageImpl<>(getMolds(manufacturer));
        when(manufacturerService.getManufacturer(0L)).thenReturn(Optional.of(manufacturer));
        when(moldService.getMoldsByManufacturer(eq(manufacturer), any())).thenReturn(page);

        var response = restTemplate.getForEntity(createUrl() + "?valmId=0", String.class);

        verify(moldService, never()).getMolds(any());
        verify(manufacturerService, times(1)).getManufacturer(0L);
        verify(moldService, times(1)).getMoldsByManufacturer(eq(manufacturer), any());

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("discmaniaMolds.json"), response.getBody());
    }

    @Test
    void givenManufacturerNotFound_whenGetMolds_thenRespondBadRequest() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        when(manufacturerService.getManufacturer(66L)).thenReturn(Optional.empty());

        var response = restTemplate.getForEntity(createUrl() + "?valmId=66", String.class);

        verify(moldService, never()).getMolds(any());
        verify(manufacturerService, times(1)).getManufacturer(66L);
        verify(moldService, never()).getMoldsByManufacturer(any(), any());

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void givenValidRequest_whenCreateMold_thenCreateMold() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var manufacturer = MANUFACTURERS.get(0);
        var dto = MoldCreateDto.builder().valmId(0L).build();
        var mold = getTestMold(manufacturer);
        when(manufacturerService.getManufacturer(0L)).thenReturn(Optional.of(manufacturer));
        when(moldService.createMold(dto, manufacturer)).thenReturn(projectionFromMold(mold));

        var response = restTemplate.postForEntity(createUrl(), dto, String.class);

        verify(manufacturerService, times(1)).getManufacturer(0L);
        verify(moldService, times(1)).createMold(dto, manufacturer);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson(adder.create("newMold.json"), response.getBody());
    }

    @Test
    void givenManufacturerNotFound_whenCreateMold_thenRespondBadRequest() {
        when(authService.getUser(any())).thenReturn(ADMIN_USER);

        var dto = MoldCreateDto.builder().valmId(0L).build();
        when(manufacturerService.getManufacturer(0L)).thenReturn(Optional.empty());

        var response = restTemplate.postForEntity(createUrl(), dto, String.class);

        verify(manufacturerService, times(1)).getManufacturer(0L);
        verify(moldService, never()).createMold(any(MoldCreateDto.class), any());

        assertEquals(SC_BAD_REQUEST, response.getStatusCodeValue());
        assertNull(response.getBody());
    }


    //HELPER METHODS

    private String createUrl() {
        return String.format("http://localhost:%d/api/molds/", port);
    }

    private Mold getTestMold(Manufacturer manufacturer) {
        var mold = new Mold();
        mold.setId(66L);
        mold.setName("New Mold");
        mold.setManufacturer(manufacturer);
        mold.setSpeed(1.0);
        mold.setGlide(2.0);
        mold.setStability(3.0);
        mold.setFade(4.0);
        return mold;
    }
}
