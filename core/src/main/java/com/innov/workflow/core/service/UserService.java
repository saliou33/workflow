package com.innov.workflow.core.service;

import com.innov.workflow.core.domain.entity.Organization;
import com.innov.workflow.core.domain.entity.Role;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.domain.repository.OrganizationRepository;
import com.innov.workflow.core.domain.repository.RoleRepository;
import com.innov.workflow.core.domain.repository.UserRepository;
import com.innov.workflow.core.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getAllUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository.findAll(pageable);
    }


    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getUsersByRole(Long id) {
        Optional<Role> role = roleRepository.findById(id);

        return userRepository.findAllByRoles(role.get());
    }

    public List<User> getUsersByOrganization(Long id) {
        Optional<Organization> organization = organizationRepository.findById(id);

        return userRepository.findAllByOrganization(organization.get());
    }

    public User addRole(Long userId, Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role not found with id: " + roleId));
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "User not found with id: " + roleId));

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            return userRepository.save(user);
        }

        throw new ApiException(HttpStatus.BAD_REQUEST, "Role already added");
    }

    public User deleteRole(Long userId, Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role not found with id: " + roleId));
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "User not found with id: " + roleId));

        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            return userRepository.save(user);
        }

        throw new ApiException(HttpStatus.BAD_REQUEST, "Role not affected to user");
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return null;
        }

        existingUser.setOrganization(user.getOrganization());
        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(user.getPassword());

        // set other properties as needed
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}        // set other properties as needed

