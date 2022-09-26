package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.GroupRepository;
import fi.bizhop.kiekkohamsteri.db.RoleRepository;
import fi.bizhop.kiekkohamsteri.db.UserRepository;
import fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.Role;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.projection.v1.LeaderProjection;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

import static fi.bizhop.kiekkohamsteri.util.Utils.*;

@Service
@RequiredArgsConstructor
public class UserService {
	final UserRepository userRepository;
	final GroupRepository groupRepository;
	final RoleRepository roleRepository;

	private Role adminRole = null;
	private final Map<Long, Role> groupAdminRoles = new HashMap<>();

	//V1 compatibility
	public User updateDetails(User user, fi.bizhop.kiekkohamsteri.dto.v1.in.UserUpdateDto dto, boolean adminRequest) {
		var dtoV2 = UserUpdateDto.fromV1(dto);
		return updateDetailsV2(user, null, dtoV2, adminRequest);
	}

	public User updateDetailsV2(User user, User authUser, UserUpdateDto dto, boolean adminRequest) {
		if(user == null) return null;

		var ignores = Utils.getNullPropertyNames(dto);
		ignores.addAll(List.of("addToGroupId", "removeFromGroupId", "addToRole", "removeFromRole", "roleGroupId"));
		if(!adminRequest) {
			ignores.add("level");
		}
		BeanUtils.copyProperties(dto, user, ignores.toArray(String[]::new));

		var addToGroupId = dto.getAddToGroupId();
		var removeFromGroupId = dto.getRemoveFromGroupId();
		var addToRole = dto.getAddToRole();
		var removeFromRole = dto.getRemoveFromRole();
		var roleGroupId = dto.getRoleGroupId();
		if(adminRequest) {
			addToGroup(user, addToGroupId);
			removeFromGroup(user, removeFromGroupId);
			addToRole(user, addToRole, roleGroupId);
			removeFromRole(user, removeFromRole, roleGroupId);
		}

		if(userIsGroupAdmin(authUser, addToGroupId)) {
			addToGroup(user, addToGroupId);
		}
		if(userIsGroupAdmin(authUser, removeFromGroupId) || user.equals(authUser)) {
			removeFromGroup(user, removeFromGroupId);
		}
		if(userIsGroupAdmin(authUser, roleGroupId)) {
			addToRole(user, addToRole, roleGroupId);
		}
		if(userIsGroupAdmin(authUser, roleGroupId) || user.equals(authUser)) {
			removeFromRole(user, removeFromRole, roleGroupId);
		}
		
		return userRepository.save(user);
	}

	public List<User> getUsersByGroupId(Long groupId) {
		var group = groupRepository.findById(groupId).orElseThrow();

		return userRepository.findAllByGroups(group);
	}


	// HELPER METHODS

	private void addToGroup(User user, Long addToGroup) {
		if (addToGroup != null) {
			var group = groupRepository.findById(addToGroup).orElseThrow();
			user.getGroups().add(group);
		}
	}

	private void removeFromGroup(User user, Long removeFromGroup) {
		if (removeFromGroup != null) {
			var group = groupRepository.findById(removeFromGroup).orElseThrow();
			user.getGroups().remove(group);
		}
	}

	private void addToRole(User user, String addToRole, Long roleGroupId) {
		if (addToRole != null) {
			if (USER_ROLE_ADMIN.equals(addToRole)) {
				user.getRoles().add(getAdminRole());
			} else if (USER_ROLE_GROUP_ADMIN.equals(addToRole) && roleGroupId != null) {
				user.getRoles().add(getGroupAdminRole(roleGroupId));
			}
		}
	}

	private void removeFromRole(User user, String removeFromRole, Long roleGroupId) {
		if (removeFromRole != null) {
			if (USER_ROLE_ADMIN.equals(removeFromRole)) {
				user.getRoles().remove(getAdminRole());
			} else if (USER_ROLE_GROUP_ADMIN.equals(removeFromRole) && roleGroupId != null) {
				user.getRoles().remove(getGroupAdminRole(roleGroupId));
			}
		}
	}

	private Role getAdminRole() {
		if(this.adminRole == null) {
			this.adminRole = roleRepository.findById(1L).orElseThrow();
		}
		return this.adminRole;
	}

	private Role getGroupAdminRole(Long groupId) {
		if(groupId == null) throw new NoSuchElementException();
		var role = groupAdminRoles.get(groupId);
		if(role == null) {
			role = roleRepository.findByGroupId(groupId).orElseThrow();
			groupAdminRoles.put(groupId, role);
		}
		return role;
	}


	// Passthrough methods to db
	// Not covered (or to be covered by unit tests)

	public User getUser(Long id) {
		return userRepository.findById(id).orElseThrow();
	}

	public List<User> getUsers() {
		return userRepository.findAllByOrderById();
	}

	public void saveUser(User user) {
		userRepository.save(user);
	}

	public void saveUsers(List<User> users) {
		userRepository.saveAll(users);
	}

	public List<LeaderProjection> getLeaders() {
		return userRepository.findByPublicDiscCountTrueOrderByDiscCountDesc();
	}

	public List<User> getUsersWithPublicList() {
		return userRepository.findByPublicListTrue();
	}
}
