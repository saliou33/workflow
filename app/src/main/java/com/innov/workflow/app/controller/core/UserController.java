package com.innov.workflow.app.controller.core;

import com.innov.workflow.app.dto.core.UserDto;
import com.innov.workflow.app.mapper.core.UserMapper;
import com.innov.workflow.core.constant.Constants;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.service.UserService;
import com.innov.workflow.core.utils.file.FileUploadUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/roles/:id")
    public ResponseEntity getAllUsersByRole(@PathVariable Long id) {
        List<UserDto> data = userMapper.mapToDtoList(userService.getAllUsers());
        return ApiResponse.success(data);
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity addRole(@PathVariable Long userId, @RequestBody Long roleId) {
        User user = userService.addRole(userId, roleId);
        return ApiResponse.success("role ajouter", userMapper.mapToDto(user));
    }

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity deleteRole(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userService.deleteRole(userId, roleId);
        return ApiResponse.success("role révoquer", userMapper.mapToDto(user));
    }


    @GetMapping("/organizations/{id}")
    public ResponseEntity getAllUsersByOrganization(@PathVariable Long id) {
        return ApiResponse.success(userService.getUsersByOrganization(id));
    }

    @PostMapping
    public ResponseEntity createUser(@RequestBody UserDto userDTO) {
        User user = userService.createUser(userMapper.mapFromDto(userDTO));
        return ApiResponse.created("utilisateur créer", user);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable Long id, @RequestBody UserDto userDTO) {
        User user = userService.updateUser(id, userMapper.mapFromDto(userDTO));
        return ApiResponse.success("utilisateur modifier", user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("Utilisateur supprimer");
    }

    @PostMapping("/avatar")
    public ResponseEntity updateAvatar(@RequestParam Long id, @RequestParam MultipartFile file) {
        User user = userService.getUserByUserId(id);
        try {
            if (!file.isEmpty()) {
                String avatar = FileUploadUtils.upload(Constants.AVATAR_PATH, file);
                user.setAvatar(avatar);
                userService.saveUser(user);

            }
        } catch (Exception e) {
            log.error("Update Avatar Exception: ", e);
            return ApiResponse.error("Upload failed");
        }
        return ApiResponse.success("Upload Successful");
    }
}
