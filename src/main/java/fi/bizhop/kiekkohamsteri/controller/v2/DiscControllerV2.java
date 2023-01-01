package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscInputDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.DiscSearchDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.DiscOutputDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.projection.v1.DiscProjection;
import fi.bizhop.kiekkohamsteri.service.*;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

import static javax.servlet.http.HttpServletResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequiredArgsConstructor
public class DiscControllerV2 extends BaseControllerV2 {
    final UserService userService;
    final DiscService discService;
    final MoldService moldService;
    final PlasticService plasticService;
    final ColorService colorService;

    @RequestMapping(value = "/discs", method = GET, produces = "application/json")
    public @ResponseBody Page<DiscOutputDto> getDiscs(
            @RequestAttribute("user") User me,
            @RequestParam(required = false) Long userId,
            HttpServletResponse response,
            Pageable pageable) {
        if(userId != null) {
            try {
                var otherUser = userService.getUser(userId);
                var groupIntersection = new HashSet<>(me.getGroups());
                groupIntersection.retainAll(otherUser.getGroups());
                if(groupIntersection.isEmpty()) {
                    response.setStatus(SC_FORBIDDEN);
                    return null;
                }
                return discService.getDiscsV2(otherUser, pageable).map(DiscOutputDto::fromDb);
            } catch (NoSuchElementException e) {
                response.setStatus(SC_BAD_REQUEST);
                return null;
            }
        }

        response.setStatus(SC_OK);
        return discService.getDiscsV2(me, pageable).map(DiscOutputDto::fromDb);
    }

    @RequestMapping(value = "/discs/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
    public @ResponseBody DiscOutputDto updateDisc(@RequestAttribute("user") User owner, @PathVariable Long id, @RequestBody DiscInputDto dto, HttpServletResponse response) {
        try {
            var newMold = moldService.getMold(dto.getMoldId()).orElse(null);
            var newPlastic = plasticService.getPlastic(dto.getPlasticId()).orElse(null);
            var newColor = colorService.getColor(dto.getColorId()).orElse(null);
            response.setStatus(SC_OK);
            return DiscOutputDto.fromDb(discService.updateDisc(dto, id, owner, newMold, newPlastic, newColor));
        }
        catch(AuthorizationException ae) {
            response.setStatus(SC_FORBIDDEN);
            return null;
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/discs/{id}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody DiscOutputDto getDisc(@RequestAttribute("user") User owner, @PathVariable Long id, HttpServletResponse response) {
        try {
            response.setStatus(SC_OK);
            return DiscOutputDto.fromDb(discService.getDiscIfPublicOrOwnV2(owner, id));
        }
        catch (AuthorizationException ae) {
            response.setStatus(SC_FORBIDDEN);
            return null;
        }
        catch (HttpResponseException hre) {
            LOG.error("{} trying to get disc id={}, not found", owner.getEmail(), id);
            response.setStatus(hre.getStatusCode());
            return null;
        }
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
