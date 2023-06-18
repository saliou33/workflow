package com.innov.workflow.core.service;

import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.domain.entity.Organization;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.domain.repository.GroupRepository;
import com.innov.workflow.core.domain.repository.OrganizationRepository;
import com.innov.workflow.core.domain.repository.UserRepository;
import com.innov.workflow.core.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final OrganizationRepository organizationRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllUserLike(String p) {
        return userRepository.findUsersByUsernameLike(p);
    }


    public Page<User> getAllUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository.findAll(pageable);
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


    public List<User> getUsersByRole(Long id) {
        Optional<Group> group = groupRepository.findById(id);

        return userRepository.findAllByGroups(group.get());
    }

    public List<User> getUsersByOrganization(Long id) {
        Optional<Organization> organization = organizationRepository.findById(id);

        return userRepository.findAllByOrganization(organization.get());
    }

    public User addRole(Long userId, Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role not found with id: " + groupId));
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "User not found with id: " + groupId));

        if (!user.getGroups().contains(group)) {
            user.getGroups().add(group);
            return userRepository.save(user);
        }

        throw new ApiException(HttpStatus.BAD_REQUEST, "Role already added");
    }

    public User deleteRole(Long userId, Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Role not found with id: " + groupId));
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "User not found with id: " + groupId));

        if (user.getGroups().contains(group)) {
            user.getGroups().remove(group);
            return userRepository.save(user);
        }

        throw new ApiException(HttpStatus.BAD_REQUEST, "Role not affected to user");
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }


    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existingUser = getUserByUserId(id);

        existingUser.setOrganization(user.getOrganization());
        existingUser.setUsername(user.getUsername());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setFullName();
        existingUser.setAvatar(user.getAvatar());
        existingUser.setTel(user.getTel());
        existingUser.setUpdatedAt(LocalDateTime.now());

        // set other properties as needed
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}        // set other properties as needed

