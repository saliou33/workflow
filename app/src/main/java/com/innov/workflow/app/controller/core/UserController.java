package com.innov.workflow.app.controller.core;

import com.innov.workflow.app.dto.core.UserDto;
import com.innov.workflow.app.mapper.core.UserMapper;
import com.innov.workflow.app.service.AuthService;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity getUsers(
            @RequestParam(name = "f", required = false) String[] fieldNames,
            @RequestParam(name = "s", required = false) String[] searchTerms,
            @RequestParam(name = "n", required = false) String[] notFieldNames,
            @RequestParam(name = "nv", required = false) String[] notValues,
            Pageable pageable
    ) {
        Page<User> data = userService.getUsers(fieldNames, searchTerms, notFieldNames, notValues, pageable);
        return ApiResponse.success(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable Long id) {
        return ApiResponse.success(userMapper.mapToDto(userService.getUserByUserId(id)));
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity getUsersByGroup(@PathVariable Long id, Pageable pageable) {
        Page<User> data = userService.getUsersByGroup(id, pageable);
        return ApiResponse.success(data);
    }

    @GetMapping("/organization/{id}")
    public ResponseEntity getUsersByOrganization(@PathVariable Long id, Pageable pageable) {
        Page<User> data = userService.getUsersByOrganization(id, pageable);
        return ApiResponse.success(data);
    }

    @PutMapping("/{userId}/role/{roleId}")
    public ResponseEntity addUserRole(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userService.addRole(userId, roleId);
        return ApiResponse.success("role added to user", userMapper.mapToDto(user));
    }

    @DeleteMapping("{userId}/roles/{roleId}")
    public ResponseEntity deleteUserRole(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userService.deleteRole(userId, roleId);
        return ApiResponse.success("role removed from user", userMapper.mapToDto(user));

    }

    @PutMapping("/{userId}/groups/{groupId}")
    public ResponseEntity addToGroup(@PathVariable Long userId, @PathVariable Long groupId) {
        User user = userService.addRole(userId, groupId);
        return ApiResponse.success("user added to group", userMapper.mapToDto(user));
    }

    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity deleteFromGroup(@PathVariable Long userId, @PathVariable Long groupId) {
        User user = userService.deleteFromGroup(userId, groupId);
        return ApiResponse.success("user deleted from group", userMapper.mapToDto(user));
    }

    @PutMapping("/{userId}/organization/{orgId}")
    public ResponseEntity addToOrganization(@PathVariable Long userId, @PathVariable Long orgId) {
        User user = userService.addToOrganization(userId, orgId);
        return ApiResponse.success("user added to organization", userMapper.mapToDto(user));
    }

    @DeleteMapping("/{userId}/organization/{orgId}")
    public ResponseEntity DeleteFromOrganization(@PathVariable Long userId, @PathVariable Long orgId) {
        User user = userService.deleteFromOrganization(userId, orgId);
        return ApiResponse.success("user deleted from organization", userMapper.mapToDto(user));
    }

    @PostMapping
    public ResponseEntity createUser(@RequestBody UserDto userDTO) {
        User userData = userMapper.mapFromDto(userDTO);
        userData.setPassword(authService.hashPassword(userData.getPassword()));
        //TODO: change this before prod build
        userData.setEnabled(true);
        User user = userService.createUser(userData);
        authService.sendVerificationToken(user);
        return ApiResponse.created("user created", user);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable Long id, @RequestBody UserDto userDTO) {
        User userData = userMapper.mapFromDto(userDTO);
        userData.setPassword(authService.hashPassword(userData.getPassword()));

        User user = userService.updateUser(id, userData);
        return ApiResponse.success("user updated", user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("user deleted");
    }
}
