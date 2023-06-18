package com.innov.workflow.app.mapper.core;

import com.innov.workflow.app.dto.core.GroupDto;
import com.innov.workflow.core.domain.entity.Group;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class GroupMapper {

    public abstract GroupDto mapToDto(Group group);

    public abstract List<GroupDto> mapToDtoList(List<Group> groupList);

    public abstract Group mapFromDto(GroupDto groupDto);
}
