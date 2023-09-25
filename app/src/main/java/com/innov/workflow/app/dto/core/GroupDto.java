package com.innov.workflow.app.dto.core;

import com.innov.workflow.core.domain.entity.Organization;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class GroupDto extends BaseDto {
    private Long id;
    @NotBlank(message = "le champs nom est vide")
    @Size(min = 3, max = 100)
    private String name;
    private String description;

    private TagDto tag;

    private Organization organization;
}
