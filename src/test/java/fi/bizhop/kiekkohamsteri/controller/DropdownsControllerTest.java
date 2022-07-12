package fi.bizhop.kiekkohamsteri.controller;

import fi.bizhop.kiekkohamsteri.SpringContextTestBase;
import fi.bizhop.kiekkohamsteri.dto.DropdownsDto;
import fi.bizhop.kiekkohamsteri.service.AuthService;
import fi.bizhop.kiekkohamsteri.service.DropdownsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static fi.bizhop.kiekkohamsteri.TestObjects.*;
import static fi.bizhop.kiekkohamsteri.TestUtils.assertEqualsJson;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DropdownsControllerTest extends SpringContextTestBase {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;
    @MockBean AuthService authService;
    @MockBean DropdownsService dropdownsService;

    @Test
    void givenUnableToAuthenticateUser_whenGetDropdowns_thenResponseCodeUnauthorized() {
        when(authService.getUser(any())).thenReturn(null);

        var response = restTemplate.getForEntity(createUrl(), String.class);

        assertEquals(SC_UNAUTHORIZED, response.getStatusCodeValue());
    }

    @Test
    void givenValidUser_whenGetDropdowns_thenGetDropdowns() {
        when(authService.getUser(any())).thenReturn(TEST_USER);

        var dto = DropdownsDto.builder()
                .valms(getManufacturersDD())
                .molds(getMoldsDD())
                .muovit(getPlasticsDD())
                .varit(getColorsDD())
                .kunto(getConditionsDD())
                .tussit(getMarkingsDD())
                .build();

        when(dropdownsService.getDropdowns(null)).thenReturn(dto);

        var response = restTemplate.getForEntity(createUrl(), String.class);

        assertEquals(SC_OK, response.getStatusCodeValue());
        assertEqualsJson("expectedDropdowns.json", response.getBody());
    }


    //HELPER METHODS

    private String createUrl() {
        return String.format("http://localhost:%d/api/dropdown", port);
    }
}
