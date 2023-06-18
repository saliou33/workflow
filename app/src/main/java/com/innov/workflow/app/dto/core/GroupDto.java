package com.innov.workflow.app.dto.core;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class GroupDto extends BaseDto {
    private Long id;

    @NotBlank(message = "le champs nom est vide")
    @Size(message = "le nom doit etre compris entre 3 et 100 caracterers", min = 3, max = 100)
    private String name;

    private RoleDto tag;


}
