package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscSearchDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.DiscOutputDto;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.service.DiscService;
import fi.bizhop.kiekkohamsteri.service.UserService;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequiredArgsConstructor
public class DiscControllerV2 extends BaseControllerV2 {
    final UserService userService;
    final DiscService discService;

    @RequestMapping(value = "/discs", method = GET, produces = "application/json")
    public @ResponseBody Page<DiscOutputDto> getDiscs(
            @RequestAttribute("user") User me,
            HttpServletResponse response,
            Pageable pageable) {
        response.setStatus(SC_OK);
        return discService.getDiscsV2(me, pageable).map(DiscOutputDto::fromDb);
    }

    @RequestMapping(value = "/discs/search", method = GET, produces = "application/json")
    public @ResponseBody List<Utils.SupportedOperation> supportedSearchOperations(
            HttpServletResponse response) {
        response.setStatus(SC_OK);
        return Utils.getSupportedOperations();
    }

    @RequestMapping(value = "/discs/search", method = POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody Page<DiscOutputDto> search(
            @RequestAttribute("user") User me,
            @RequestBody DiscSearchDto searchDto,
            HttpServletResponse response,
            Pageable pageable) {
        try {
            response.setStatus(SC_OK);
            return discService.search(me, pageable, searchDto).map(DiscOutputDto::fromDb);
        } catch (HttpResponseException hre) {
            response.setStatus(SC_BAD_REQUEST);
            return null;
        }
    }
}
