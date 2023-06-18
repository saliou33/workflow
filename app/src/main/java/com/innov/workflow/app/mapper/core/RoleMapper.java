package com.innov.workflow.app.mapper.core;

import com.innov.workflow.app.dto.core.RoleDto;
import com.innov.workflow.core.domain.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class RoleMapper {

    public abstract RoleDto mapToDto(Role role);

    public abstract List<RoleDto> mapToDtoList(List<Role> roleList);

    public abstract Role mapFromDto(RoleDto roleDto);
}
