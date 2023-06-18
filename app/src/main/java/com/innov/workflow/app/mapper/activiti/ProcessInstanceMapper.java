package com.innov.workflow.app.mapper.activiti;


import com.innov.workflow.app.dto.activiti.ProcessInstanceDto;
import org.activiti.engine.runtime.ProcessInstance;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProcessInstanceMapper {
    public abstract ProcessInstanceDto mapToDto(ProcessInstance processInstance);

    public abstract List<ProcessInstanceDto> mapToDtoList(List<ProcessInstance> processInstances);
}
