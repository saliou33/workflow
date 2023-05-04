package com.innov.workflow.app.dto.core;

import com.innov.workflow.core.domain.entity.Role;
import com.innov.workflow.core.domain.entity.RoleTag;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RoleTagDTO extends BaseDTO {
    private Long id;

    @NotBlank(message = "le champs nom est vide")
    @Size(message = "le nom doit etre compris entre 3 et 50 caracterers", min = 3, max = 50)
    private String name;

    public static RoleTagDTO fromEntity(RoleTag tag) {
        RoleTagDTO dto = new RoleTagDTO();
        dto.setId(tag.getTagId());
        dto.setName(tag.getName());
        return dto;
    }

    public RoleTag toEntity() {
        RoleTag tag = new RoleTag();
        tag.setTagId(this.getId());
        tag.setName(this.getName());
        return tag;
    }

}