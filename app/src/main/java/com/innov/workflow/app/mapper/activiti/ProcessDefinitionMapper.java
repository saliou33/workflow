package com.innov.workflow.app.mapper.activiti;

import com.innov.workflow.app.dto.activiti.ProcessDefinitionDto;
import lombok.RequiredArgsConstructor;
import org.activiti.engine.repository.ProcessDefinition;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
@RequiredArgsConstructor
public abstract class ProcessDefinitionMapper {

    public abstract ProcessDefinitionDto mapToDto(ProcessDefinition processDefinition);

    public abstract List<ProcessDefinitionDto> mapToDtoList(List<ProcessDefinition> processDefinition);
}
