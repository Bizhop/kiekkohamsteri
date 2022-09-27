package fi.bizhop.kiekkohamsteri.controller.v2;

import fi.bizhop.kiekkohamsteri.dto.v2.in.CompleteGroupRequestDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.GroupCreateDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.GroupRequestDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.GroupDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.GroupRequestOutputDto;
import fi.bizhop.kiekkohamsteri.dto.v2.out.UserOutputDto;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.GroupRequest;
import fi.bizhop.kiekkohamsteri.model.Role;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.service.GroupService;
import fi.bizhop.kiekkohamsteri.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static fi.bizhop.kiekkohamsteri.model.GroupRequest.Status.COMPLETED;
import static fi.bizhop.kiekkohamsteri.util.Utils.*;
import static javax.servlet.http.HttpServletResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequiredArgsConstructor
public class GroupController extends BaseControllerV2 {
    final UserService userService;
    final GroupService groupService;

    @RequestMapping(value = "/groups", method = GET, produces = "application/json")
    public @ResponseBody List<GroupDto> getGroups(
            @RequestAttribute("user") User user,
            HttpServletResponse response) {
        response.setStatus(SC_OK);
        return groupService.getGroups()
                .stream()
                .map(GroupDto::fromDb)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/groups", method = POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody GroupDto createGroup(
            @RequestAttribute("user") User user,
            @RequestBody GroupCreateDto dto,
            HttpServletResponse response) {
        response.setStatus(SC_OK);
        try {
            var group = groupService.createGroup(user, dto);
            userService.saveUser(user);
            return GroupDto.fromDb(group);
        } catch (HttpResponseException e) {
            response.setStatus(e.getStatusCode());
            return null;
        }
    }

    @RequestMapping(value = "/groups/{groupId}/requests", method = POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody GroupRequestOutputDto registerGroupRequest(
            @RequestAttribute("user") User user,
            @PathVariable Long groupId,
            @RequestBody GroupRequestDto dto,
            HttpServletResponse response) {
        if(dto == null || dto.getTargetUserId() == null) {
            response.setStatus(SC_BAD_REQUEST);
            return null;
        }

        response.setStatus(SC_OK);
        try {
            var target = userService.getUser(dto.getTargetUserId());
            if(userIsAdmin(user) || userIsGroupAdmin(user, groupId)) {
                return groupService.getGroup(groupId).map(group -> {
                    var request = new GroupRequest(group, user, target, dto.getType(), COMPLETED, dto.getInfo());
                    try {
                        var modifiedUser = groupService.executeAction(request);
                        userService.saveUser(modifiedUser);
                        return GroupRequestOutputDto.builderFromDb(request)
                                .info("Request completed")
                                .build();
                    } catch (HttpResponseException e) {
                        response.setStatus(e.getStatusCode());
                        return GroupRequestOutputDto.builder().error(e.getMessage()).build();
                    }
                }).orElseGet(() -> {
                    response.setStatus(SC_NOT_FOUND);
                    return GroupRequestOutputDto.builder().error("Group not found").build();
                });
            }
            return GroupRequestOutputDto.fromDb(groupService.registerRequest(user, target, groupId, dto));
        } catch (NoSuchElementException e) {
            response.setStatus(SC_NOT_FOUND);
            return GroupRequestOutputDto.builder().error("Target user not found").build();
        } catch (HttpResponseException e) {
            response.setStatus(e.getStatusCode());
            return GroupRequestOutputDto.builder().error(e.getMessage()).build();
        }
    }

    @RequestMapping(value = "/groups/requests", method = GET, produces = "application/json")
    public @ResponseBody List<GroupRequestOutputDto> getGroupRequests(
            @RequestAttribute("user") User user,
            HttpServletResponse response) {
        response.setStatus(SC_OK);

        var result = userIsAdmin(user)
                ? getGroupRequests()
                : getGroupRequests(extractAdministeredGroupIds(user));

         return result.stream()
                .map(GroupRequestOutputDto::fromDb)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/groups/{groupId}/requests/{requestId}", method = POST, consumes = "application/json", produces = "application/json")
    public @ResponseBody UserOutputDto completeRequest(
            @RequestAttribute("user") User user,
            @PathVariable Long groupId,
            @PathVariable Long requestId,
            @RequestBody CompleteGroupRequestDto dto,
            HttpServletResponse response) {
        if(dto == null) {
            response.setStatus(SC_BAD_REQUEST);
            return UserOutputDto.builder().error("Invalid request body").build();
        }

        if(!userIsAdmin(user) && !userIsGroupAdmin(user, groupId)) {
            response.setStatus(SC_FORBIDDEN);
            return null;
        }

        response.setStatus(SC_OK);
        try {
            var request = groupService.getGroupRequest(requestId)
                    .orElseThrow(() -> new HttpResponseException(SC_NOT_FOUND, "Request not found"));
            var modifiedUser = groupService.completeRequest(request, dto.isConfirm());
            userService.saveUser(modifiedUser);
            return UserOutputDto.fromDb(modifiedUser);
        } catch (HttpResponseException e) {
            response.setStatus(e.getStatusCode());
            return UserOutputDto.builder().error(e.getMessage()).build();
        }
    }

    private List<GroupRequest> getGroupRequests() {
        return groupService.getGroupRequests();
    }

    private List<GroupRequest> getGroupRequests(Set<Long> groupIds) {
        return groupService.getGroupRequests(groupIds);
    }

    private Set<Long> extractAdministeredGroupIds(User user) {
        return user.getRoles().stream()
                .filter(role -> USER_ROLE_GROUP_ADMIN.equals(role.getName()))
                .map(Role::getGroupId)
                .collect(Collectors.toSet());
    }
}
