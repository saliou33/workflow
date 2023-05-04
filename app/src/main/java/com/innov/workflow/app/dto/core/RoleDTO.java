package com.innov.workflow.app.dto.core;

import com.innov.workflow.core.domain.entity.Role;
import com.innov.workflow.core.domain.entity.RoleTag;
import com.innov.workflow.core.domain.entity.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class RoleDTO extends BaseDTO {
    private Long id;

    @NotBlank(message = "le champs nom est vide")
    @Size(message = "le nom doit etre compris entre 3 et 100 caracterers", min = 3, max = 100)
    private String name;

    private RoleTagDTO tag;

    public static RoleDTO fromEntity(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.fromBaseEntity(role);
        dto.setId(role.getRoleId());
        dto.setName(role.getName());
        dto.setTag(RoleTagDTO.fromEntity(role.getTag()));
        return dto;
    }

    public Role toEntity() {
        Role role = new Role();
        role.setRoleId(this.getId());
        role.setName(this.getName());
        role.setTag(this.getTag().toEntity());
        return role;
    }
    
}
