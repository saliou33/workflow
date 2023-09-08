package com.innov.workflow.app.dto.core;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class TagDto extends BaseDto {
    private Long id;

    @NotBlank(message = "le champs nom est vide")
    @Size(message = "le nom doit etre compris entre 3 et 50 caracterers", min = 3, max = 50)
    private String name;


}