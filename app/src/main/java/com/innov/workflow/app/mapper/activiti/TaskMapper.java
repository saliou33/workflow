package com.innov.workflow.app.mapper.activiti;

import com.innov.workflow.app.dto.activiti.TaskDto;
import org.activiti.engine.task.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")

public abstract class TaskMapper {
    public abstract TaskDto mapToDto(Task task);

    public abstract List<TaskDto> mapToDtoList(List<Task> task);
}
