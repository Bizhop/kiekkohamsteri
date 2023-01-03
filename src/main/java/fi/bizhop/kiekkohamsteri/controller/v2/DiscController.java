package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.controller.BaseControllerV2;
import fi.bizhop.kiekkohamsteri.dto.v2.in.*;
import fi.bizhop.kiekkohamsteri.dto.v2.out.BuyOutputDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.DiscOutputDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.MoldOutputDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.PlasticOutputDto;
import fi.bizhop.kiekkohamsteri.exception.AuthorizationException;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.service.*;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

import static javax.servlet.http.HttpServletResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequiredArgsConstructor
public class DiscController extends BaseControllerV2 {
    final UserService userService;
    final ManufacturerService manufacturerService;
    final DiscService discService;
    final MoldService moldService;
    final PlasticService plasticService;
    final ColorService colorService;
    final UploadService uploadService;
    final BuyService buyService;
    final Clock clock;


    //DISCS

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

    @RequestMapping(value = "/discs/for-sale", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Page<DiscOutputDto> getDiscsForSale(HttpServletResponse response, Pageable pageable) {
        response.setStatus(SC_OK);
        return discService.getDiscsForSaleV2(pageable).map(DiscOutputDto::fromDb);
    }

    @RequestMapping(value = "/discs/lost", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Page<DiscOutputDto> getLost(HttpServletResponse response, Pageable pageable) {
        response.setStatus(SC_OK);
        return discService.getLostV2(pageable).map(DiscOutputDto::fromDb);
    }

    @RequestMapping(value = "/discs", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody DiscOutputDto createDisc(@RequestAttribute("user") User owner, @RequestBody UploadDto dto, HttpServletResponse response) {
        if(invalidUploadDto(dto)) {
            response.setStatus(SC_BAD_REQUEST);
            return null;
        }

        var disc = discService.newDisc(
                owner,
                moldService.getDefaultMold(),
                plasticService.getDefaultPlastic(),
                colorService.getDefaultColor());

        var image = String.format("%s-%d", owner.getUsername(), disc.getId());
        try {
            uploadService.upload(dto, image);
            response.setStatus(SC_OK);
            disc.setImage(image);
            return DiscOutputDto.fromDb(discService.saveDisc(disc));
        }
        catch (IOException e) {
            LOG.error("Cloudinary error uploading image", e);

            //if image upload fails, delete the created disc
            discService.deleteDiscById(disc.getId());
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/discs/{id}/update-image", method = RequestMethod.PATCH, produces = "application/json", consumes = "application/json")
    public @ResponseBody DiscOutputDto updateImage(@RequestAttribute("user") User owner, @PathVariable Long id, @RequestBody UploadDto dto, HttpServletResponse response) {
        if(invalidUploadDto(dto)) {
            response.setStatus(SC_BAD_REQUEST);
            return null;
        }

        try {
            var disc = discService.getDisc(owner, id);
            var image = disc.getImage();
            //if image name already has more than one "-", it has been updated previously
            // then replace timestamp with new one
            var timestamp = clock.instant().toEpochMilli();
            var newImage = StringUtils.countOccurrencesOf(image, "-") > 1
                    ? image.substring(0, image.lastIndexOf("-")) + "-" + timestamp
                    : image + "-" + timestamp;

            uploadService.upload(dto, newImage);
            disc.setImage(newImage);
            response.setStatus(SC_NO_CONTENT);
            return DiscOutputDto.fromDb(discService.saveDisc(disc));
        }
        catch (AuthorizationException e) {
            LOG.error("{} trying to update someone else's disc", owner.getEmail());
            response.setStatus(SC_FORBIDDEN);
            return null;
        }
        catch (IOException e) {
            LOG.error("Cloudinary error uploading image", e);
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
            return null;
        }
        catch (NoSuchElementException e) {
            LOG.error("Disc not found, id={}", id);
            response.setStatus(SC_NOT_FOUND);
            return null;
        }
    }

    @RequestMapping(value = "/discs/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
    public @ResponseBody DiscOutputDto updateDisc(@RequestAttribute("user") User owner, @PathVariable Long id, @RequestBody DiscInputDto dto, HttpServletResponse response) {
        try {
            var newMold = moldService.getMold(dto.getMoldId());
            var newPlastic = plasticService.getPlastic(dto.getPlasticId());
            var newColor = colorService.getColor(dto.getColorId());
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
        catch (HttpResponseException e) {
            LOG.error("{} trying to get disc id={}, not found", owner.getEmail(), id);
            response.setStatus(e.getStatusCode());
            return null;
        }
        catch (NoSuchElementException e) {
            LOG.error("Disc not found, id={}", id);
            response.setStatus(SC_NOT_FOUND);
            return null;
        }
    }

    @RequestMapping(value = "/discs/{id}", method = RequestMethod.DELETE)
    public void deleteDisc(@RequestAttribute("user") User owner, @PathVariable Long id, HttpServletResponse response) {
        try {
            discService.deleteDisc(id, owner);
            response.setStatus(SC_NO_CONTENT);
        }
        catch(AuthorizationException ae) {
            response.setStatus(SC_FORBIDDEN);
        }
    }

    @RequestMapping(value = "/discs/{id}/buy", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody BuyOutputDto buyDisc(@RequestAttribute("user") User user, @PathVariable Long id, HttpServletResponse response) {
        try {
            var disc = discService.getDisc(id);

            response.setStatus(SC_OK);
            var buy = buyService.buyDisc(user, disc);
            return BuyOutputDto.fromDb(buy);
        }
        catch (HttpResponseException e) {
            LOG.error(e.getMessage());
            response.setStatus(e.getStatusCode());
            return null;
        }
    }

    @RequestMapping(value = "/discs/{id}/found", method = RequestMethod.PATCH)
    public void markFound(@RequestAttribute("user") User user, @PathVariable Long id, HttpServletResponse response) {
        try {
            discService.handleFoundDisc(user, id);
            response.setStatus(SC_NO_CONTENT);
        } catch (HttpResponseException e) {
            LOG.error(e.getMessage());
            response.setStatus(e.getStatusCode());
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


    //MOLDS

    @RequestMapping(value="/discs/molds", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody Page<MoldOutputDto> getMolds(@RequestParam(required=false) Long manufacturerId, Pageable pageable, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        if(manufacturerId == null) return moldService.getMolds(pageable).map(MoldOutputDto::fromDb);

        return manufacturerService.getManufacturer(manufacturerId)
                .map(m -> moldService.getMoldsByManufacturer(m, pageable)
                        .map(MoldOutputDto::fromDb))
                .orElseGet(() -> {
                    LOG.warn("Manufacturer not found, id={}", manufacturerId);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return null;
                });
    }

    @RequestMapping(value="/discs/molds", method=RequestMethod.POST, consumes="application/json")
    public @ResponseBody MoldOutputDto createMold(@RequestBody MoldCreateDto dto, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);

        return manufacturerService.getManufacturer(dto.getManufacturerId())
                .map(m -> MoldOutputDto.fromDb(moldService.createMold(dto, m)))
                .orElseGet(() -> {
                    LOG.warn("Manufacturer not found, id={}", dto.getManufacturerId());
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return null;
                });
    }


    //PLASTICS

    @RequestMapping(value="/discs/plastics", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody Page<PlasticOutputDto> getPlastics(@RequestParam(required=false) Long manufacturerId, Pageable pageable, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        if(manufacturerId == null) return plasticService.getPlastics(pageable).map(PlasticOutputDto::fromDb);

        return manufacturerService.getManufacturer(manufacturerId)
                .map(m -> plasticService.getPlasticsByManufacturer(m, pageable)
                        .map(PlasticOutputDto::fromDb))
                .orElseGet(() -> {
                    LOG.warn("Manufacturer not found, id={}", manufacturerId);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return null;
                });
    }

    @RequestMapping(value="/discs/plastics", method=RequestMethod.POST, consumes="application/json")
    public @ResponseBody PlasticOutputDto createMuovi(@RequestBody PlasticCreateDto dto, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);

        return manufacturerService.getManufacturer(dto.getManufacturerId())
                .map(m -> PlasticOutputDto.fromDb(plasticService.createPlastic(dto, m)))
                .orElseGet(() -> {
                    LOG.warn("Manufacturer not found, id={}", dto.getManufacturerId());
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return null;
                });
    }


    //HELPER METHODS

    private boolean invalidUploadDto(UploadDto dto) {
        return dto.getData() == null;
    }
}
