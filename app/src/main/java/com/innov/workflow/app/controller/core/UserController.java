package com.innov.workflow.app.controller.core;

import com.innov.workflow.app.dto.core.UserDto;
import com.innov.workflow.app.mapper.core.UserMapper;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity getAllUsers() {
        //List<UserDTO> data = UserDTO.toList(userService.getAllUsers());
        List<UserDto> data = userMapper.mapToDtoList(userService.getAllUsers());
        return ApiResponse.success(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable Long id) {
        return ApiResponse.success(userMapper.mapToDto(userService.getUserByUserId(id)));
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity getAllUsersByGroup(@PathVariable Long id) {
        List<UserDto> data = userMapper.mapToDtoList(userService.getUsersByGroup(id));
        return ApiResponse.success(data);
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity addRole(@PathVariable Long userId, @RequestBody Long roleId ) {
        User user = userService.addRole(userId, roleId);
        return ApiResponse.success("role added to user", userMapper.mapToDto(user));

    }

    @DeleteMapping("{userId}/roles")
    public ResponseEntity deleteRole(@PathVariable Long userId, @RequestBody Long roleId) {
        User user = userService.deleteRole(userId, roleId);
        return ApiResponse.success("role removed from user", userMapper.mapToDto(user));

    }

    @PostMapping("/{userId}/groups")
    public ResponseEntity addToGroup(@PathVariable Long userId, @RequestBody Long groupId) {
        User user = userService.addRole(userId, groupId);
        return ApiResponse.success("user added to group", userMapper.mapToDto(user));
    }

    @DeleteMapping("/{userId}/groups")
    public ResponseEntity deleteFromGroup(@PathVariable Long userId, @RequestBody Long groupId) {
        User user = userService.deleteFromGroup(userId, groupId);
        return ApiResponse.success("user deleted from group", userMapper.mapToDto(user));
    }

    @GetMapping("/organizations/{id}")
    public ResponseEntity getAllUsersByOrganization(@PathVariable Long id) {
        return ApiResponse.success(userService.getUsersByOrganization(id));
    }

    @PostMapping
    public ResponseEntity createUser(@RequestBody UserDto userDTO) {
        User user = userService.createUser(userMapper.mapFromDto(userDTO));
        return ApiResponse.created("user created", user);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable Long id, @RequestBody UserDto userDTO) {
        User user = userService.updateUser(id, userMapper.mapFromDto(userDTO));
        return ApiResponse.success("user updated", user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("user deleted");
    }
}
