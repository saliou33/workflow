package com.innov.workflow.app.dto.core;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class OrganizationDto extends BaseDto {

    private Long id;

    @NotBlank(message = "le champs nom est vide")
    @Min(message = "le nom doit avoir plus de 3 caracteres", value = 3)
    private String name;

    private String description;

    private String avatar;

}

