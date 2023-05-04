package com.innov.workflow.app.dto.core;

import com.innov.workflow.core.domain.entity.Organization;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class OrganizationDTO extends BaseDTO {

    private Long id;

    @NotBlank(message = "le champs nom est vide")
    @Min(message = "le nom doit avoir plus de 3 caracteres", value = 3)
    private String name;

    private String description;

    public static OrganizationDTO fromEntity(Organization organization) {
        OrganizationDTO dto = new OrganizationDTO();
        dto.fromBaseEntity(organization);
        dto.setId(organization.getOrgId());
        dto.setName(organization.getName());
        dto.setDescription(organization.getDescription());
        return dto;
    }

    public Organization toEntity() {
        Organization organization = new Organization();
        organization.setOrgId(this.getId());
        organization.setName(this.getName());
        organization.setDescription(this.getDescription());
        return organization;
    }

    // getters and setters omitted for brevity
}

