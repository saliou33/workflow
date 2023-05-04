package com.innov.workflow.app.dto.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innov.workflow.core.domain.entity.Role;
import com.innov.workflow.core.domain.entity.User;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDTO extends BaseDTO {
    private Long id;

    @NotBlank(message = "le champs nom est vide")
    @Size(message = "le nom doit être compris entre 3 et 100 caractéres")
    private String name;

    @NotBlank(message = "le champs username est vide")
    @Size(min = 3, max = 60, message = "le username doit être compris entre 3 et 60 caractéres")
    private String username;

    @Size(max = 100, message = "le champs email doit avoir au max 100 caractéres")
    @Email(message = "l'email est invalide")
    @NotBlank(message = "le champs email est vide")
    private String email;

    @NotBlank(message = "le champs mot de passe est vide")
    @JsonIgnore
    private String password;

    @Pattern(regexp = "^\\d{10}$", message = "le numero de téléphone est invalide")
    private String tel;

    private List<RoleDTO> roles;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.fromBaseEntity(user);
        dto.setId(user.getUserId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setTel(user.getTel());


        List<RoleDTO> roleDTOs = user.getRoles().stream().map(RoleDTO::fromEntity).collect(Collectors.toList());
        dto.setRoles(roleDTOs);
        return dto;
    }


    public static List<UserDTO> toList(List<User> userList) {
        List<UserDTO> userDTOS = new ArrayList<UserDTO>();
        for (User u : userList) {
            userDTOS.add(UserDTO.fromEntity(u));
        }

        return userDTOS;
    }

    public User toEntity() {
        User user = new User();
        user.setUserId(this.getId());
        user.setName(this.getName());
        user.setEmail(this.getEmail());

        List<Role> roles = this.getRoles().stream().map(RoleDTO::toEntity).collect(Collectors.toList());
        user.setRoles(roles);
        return user;
    }
}
