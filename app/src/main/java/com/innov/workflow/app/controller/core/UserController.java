package com.innov.workflow.app.controller.core;

import com.innov.workflow.app.dto.PaginationDTO;
import com.innov.workflow.app.dto.core.UserDTO;
import com.innov.workflow.core.constant.Constants;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.service.UserService;
import com.innov.workflow.core.utils.file.FileUploadUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity getAllUsers() {
        List<UserDTO> data = UserDTO.toList(userService.getAllUsers());
        return ApiResponse.success(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @GetMapping("/roles/:id")
    public ResponseEntity getAllUsersByRole(@PathVariable Long id) {
        List<UserDTO> data = UserDTO.toList(userService.getUsersByRole(id));
        return ApiResponse.success(data);
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity addRole(@PathVariable Long userId, @RequestBody Long roleId) {
        User user = userService.addRole(userId, roleId);
        return ApiResponse.success("role ajouter", UserDTO.fromEntity(user));
    }

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity deleteRole(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userService.deleteRole(userId, roleId);
        return ApiResponse.success("role révoquer", user);
    }

    @PostMapping("/pages")
    public ResponseEntity getAllUsersByPage(PaginationDTO page) {
        Page<User> data = userService.getAllUsers(page.getPageNumber(), page.getPageSize());
        return ApiResponse.success(UserDTO.toList(data.getContent()));
    }

    @GetMapping("/organizations/{id}")
    public ResponseEntity getAllUsersByOrganization(@PathVariable Long id) {
        return ApiResponse.success(userService.getUsersByOrganization(id));
    }

    @PostMapping
    public ResponseEntity createUser(@RequestBody UserDTO userDTO) {
        return ApiResponse.created("utilisateur créer", userService.createUser(userDTO.toEntity()));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = userService.updateUser(id, userDTO.toEntity());
        return ApiResponse.success("utilisateur modifier", user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("Utilisateur supprimer");
    }

    @PostMapping("/avatar")
    public ResponseEntity updateAvatar(@RequestParam Long id, @RequestParam MultipartFile file) {
        User user = userService.getUserById(id);
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
