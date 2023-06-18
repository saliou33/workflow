package com.innov.workflow.app.mapper.core;

import com.innov.workflow.app.dto.core.OrganizationDto;
import com.innov.workflow.core.domain.entity.Organization;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class OrganizationMapper {

    public abstract OrganizationDto mapToDto(Organization organization);

    public abstract List<OrganizationDto> mapToDtoList(List<Organization> organizationList);

    public abstract Organization mapFromDto(OrganizationDto OrganizationDto);
}

