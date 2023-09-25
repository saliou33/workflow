package com.innov.workflow.core.service;

import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.domain.entity.Organization;
import com.innov.workflow.core.domain.entity.SysRole;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.domain.repository.GroupRepository;
import com.innov.workflow.core.domain.repository.OrganizationRepository;
import com.innov.workflow.core.domain.repository.SysRoleRepository;
import com.innov.workflow.core.domain.repository.UserRepository;
import com.innov.workflow.core.exception.ApiException;
import com.innov.workflow.core.specification.SearchSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final OrganizationRepository organizationRepository;
    private final SysRoleRepository roleRepository;

    public List<User> getUserByUsernameLike(String p) {
        return userRepository.findUsersByUsernameLike(p);
    }

    public Page<User> getUsers(String[] fieldNames, String[] searchTerms, String[] notFieldNames, String[] notValues, Pageable pageable) {
        SearchSpecification<User> specification = new SearchSpecification<>();
        return userRepository.findAll(specification.searchByFields(fieldNames, searchTerms, notFieldNames, notValues), pageable);
    }

    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "User not found with id: " + userId));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "user not found with username: " + username));
    }

    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Page<User> getUsersByGroup(Long id, Pageable pageable) {
        Group group = groupRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "group not found"));

        return userRepository.findAllByGroups(group, pageable);
    }

    public Page<User> getUsersByOrganization(Long id, Pageable pageable) {
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "organization not found"));

        return userRepository.findAllByOrganization(organization, pageable);
    }

    public User addRole(Long userId, Long roleId) {
        SysRole role = roleRepository.findById(roleId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role not found"));
        User user = getUserByUserId(userId);

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            return userRepository.save(user);
        }

        throw new ApiException(HttpStatus.BAD_REQUEST, "role already added to user");
    }

    public User deleteRole(Long userId, Long roleId) {
        SysRole role = roleRepository.findById(roleId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role not found"));
        User user = getUserByUserId(userId);

        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            return userRepository.save(user);
        }

        throw new ApiException(HttpStatus.BAD_REQUEST, "role not found with user");
    }

    public User addToGroup(Long userId, Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Group not found with id: " + groupId));
        User user = getUserByUserId(userId);
        if (!user.getGroups().contains(group)) {
            user.getGroups().add(group);
            return userRepository.save(user);
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "User already added to group");
    }

    public User deleteFromGroup(Long userId, Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Group not found with id: " + groupId));
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "User not found with id: " + groupId));

        if (user.getGroups().contains(group)) {
            user.getGroups().remove(group);
            return userRepository.save(user);
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "User deleted from group");
    }


    public User deleteFromOrganization(Long userId, Long orgId) {
        User user = getUserByUserId(userId);
        if (user.getOrganization() != null) {
            if (user.getOrganization().getId() == orgId) {
                user.setOrganization(null);
                return userRepository.save(user);
            }
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "User not affected to organization");
    }


    public User addToOrganization(Long userId, Long orgId) {
        User user = getUserByUserId(userId);
        Organization org = organizationRepository.findById(orgId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Organization not found"));

        if (user.getOrganization() != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User already affected to an organization");

        } else {
            user.setOrganization(org);
            return userRepository.save(user);
        }
    }


    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existingUser = getUserByUserId(id);

        existingUser.setUsername(user.getUsername());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAvatar(user.getAvatar());
        existingUser.setTel(user.getTel());
        existingUser.setUpdatedAt(LocalDateTime.now());
        existingUser.setPassword(user.getPassword());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "User not found with id: " + id));
        user.setDeleted(true);
        userRepository.deleteById(id);
    }
}

