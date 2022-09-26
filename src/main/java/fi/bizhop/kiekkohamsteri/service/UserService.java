package fi.bizhop.kiekkohamsteri.service;

import fi.bizhop.kiekkohamsteri.db.GroupRepository;
import fi.bizhop.kiekkohamsteri.db.RoleRepository;
import fi.bizhop.kiekkohamsteri.db.UserRepository;
import fi.bizhop.kiekkohamsteri.dto.v2.in.UserUpdateDto;
import fi.bizhop.kiekkohamsteri.model.Role;
import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static fi.bizhop.kiekkohamsteri.util.Utils.USER_ROLE_ADMIN;

@Service
@RequiredArgsConstructor
public class UserService {
	final UserRepository userRepository;
	final GroupRepository groupRepository;
	final RoleRepository roleRepository;

	private Role adminRole = null;

	public User updateDetails(User user, UserUpdateDto dto, boolean adminRequest) {
		if(user == null) return null;

		var ignores = Utils.getNullPropertyNames(dto);
		ignores.addAll(List.of("addToRole", "removeFromRole"));

		BeanUtils.copyProperties(dto, user, ignores.toArray(String[]::new));

		if(adminRequest) {
			if (USER_ROLE_ADMIN.equals(dto.getAddToRole())){
				addAdminRole(user);
			}
			if(USER_ROLE_ADMIN.equals(dto.getRemoveFromRole())) {
				removeAdminRole(user);
			}
		}

		var removeFromGroupId = dto.getRemoveFromGroupId();
		if(removeFromGroupId != null) {
			removeFromGroup(user, removeFromGroupId);
		}

		return userRepository.save(user);
	}

	public List<User> getUsersByGroupId(Long groupId) {
		var group = groupRepository.findById(groupId).orElseThrow();

		return userRepository.findAllByGroups(group);
	}

	// HELPER METHODS

	private void removeFromGroup(User user, Long removeFromGroup) {
		if (removeFromGroup != null) {
			var group = groupRepository.findById(removeFromGroup).orElseThrow();
			user.getGroups().remove(group);
		}
	}

	private void addAdminRole(User user) {
		user.getRoles().add(getAdminRole());
	}

	private void removeAdminRole(User user) {
		user.getRoles().remove(getAdminRole());
	}

	private Role getAdminRole() {
		if(this.adminRole == null) {
			this.adminRole = roleRepository.findById(1L).orElseThrow();
		}
		return this.adminRole;
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
}
