package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.GroupRepository;
import fi.bizhop.kiekkohamsteri.db.GroupRequestRepository;
import fi.bizhop.kiekkohamsteri.db.RoleRepository;
import fi.bizhop.kiekkohamsteri.dto.v2.in.GroupCreateDto;
import fi.bizhop.kiekkohamsteri.dto.v2.in.GroupRequestDto;
import fi.bizhop.kiekkohamsteri.exception.HttpResponseException;
import fi.bizhop.kiekkohamsteri.model.Group;
import fi.bizhop.kiekkohamsteri.model.GroupRequest;
import fi.bizhop.kiekkohamsteri.model.Role;
import fi.bizhop.kiekkohamsteri.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static fi.bizhop.kiekkohamsteri.model.GroupRequest.Status.*;
import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_GROUP_ADMIN;
import static javax.servlet.http.HttpServletResponse.*;

@Service
@RequiredArgsConstructor
public class GroupService {
    final GroupRepository groupRepository;
    final GroupRequestRepository groupRequestRepository;
    final RoleRepository roleRepository;

    public GroupRequest registerRequest(User source, User target, Long groupId, GroupRequestDto dto) throws HttpResponseException {
        var group = groupRepository.findById(groupId)
                .orElseThrow(() -> new HttpResponseException(SC_NOT_FOUND, "Group not found"));

        var request = new GroupRequest(group, source, target, dto.getType(), REQUESTED, dto.getInfo());
        return groupRequestRepository.save(request);
    }

    public User completeRequest(GroupRequest request, boolean confirm) throws HttpResponseException {
        if(!REQUESTED.equals(request.getStatus())) throw new HttpResponseException(SC_BAD_REQUEST, "Request already completed");

        if(confirm) {
            executeAction(request);
        }

        request.setStatus(confirm ? COMPLETED : REJECTED);
        groupRequestRepository.save(request);
        return request.getTarget();
    }

    public User executeAction(GroupRequest request) throws HttpResponseException {
        var user = request.getTarget();
        var role = roleRepository.findByGroupId(request.getGroup().getId())
                .orElseThrow(() -> new HttpResponseException(SC_NOT_FOUND, "Group admin role not found"));

        switch (request.getType()) {
            case JOIN:
                user.getGroups().add(request.getGroup());
                break;
            case KICK:
                user.getGroups().remove(request.getGroup());
                break;
            case PROMOTE:
                user.getRoles().add(role);
                break;
            case DEMOTE:
                user.getRoles().remove(role);
                break;
        }

        return user;
    }

    public Group createGroup(User user, GroupCreateDto dto) throws HttpResponseException {
        if(dto == null || dto.getName() == null) throw new HttpResponseException(SC_BAD_REQUEST, "Invalid group create request");

        var group = new Group(dto.getName());
        groupRepository.save(group);

        var groupAdminRole = new Role(USER_ROLE_GROUP_ADMIN, group.getId());
        roleRepository.save(groupAdminRole);

        user.getGroups().add(group);
        user.getRoles().add(groupAdminRole);

        return group;
    }

    public List<GroupRequest> getGroupRequests(Long groupId) throws HttpResponseException {
        var group = groupRepository.findById(groupId).orElseThrow(() -> new HttpResponseException(SC_NOT_FOUND, "Group not found"));
        return groupRequestRepository.findByGroup(group);
    }

    // Passthrough methods to db
    // Not covered (or to be covered by unit tests)

    public List<Group> getGroups() { return groupRepository.findAll(); }

    public Optional<Group> getGroup(Long groupId) { return groupRepository.findById(groupId); }

    public List<GroupRequest> getGroupRequests() { return groupRequestRepository.findAll(); }

    public Optional<GroupRequest> getGroupRequest(Long id) { return groupRequestRepository.findById(id); }
}
