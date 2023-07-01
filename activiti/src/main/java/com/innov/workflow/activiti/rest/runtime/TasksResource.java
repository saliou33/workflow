package com.innov.workflow.activiti.rest.runtime;


import com.innov.workflow.activiti.model.runtime.CreateTaskRepresentation;
import com.innov.workflow.activiti.model.runtime.TaskRepresentation;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")

public class TasksResource {
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected IdentityService identityService;

    public TasksResource() {
    }

    @RequestMapping(
            value = {"/activiti/tasks"},
            method = {RequestMethod.POST}
    )
    public TaskRepresentation createNewTask(@RequestBody CreateTaskRepresentation taskRepresentation, HttpServletRequest request) {
        if (StringUtils.isEmpty(taskRepresentation.getName())) {
            throw new BadRequestException("Task name is required");
        } else {
            Task task = this.taskService.newTask();
            task.setName(taskRepresentation.getName());
            task.setDescription(taskRepresentation.getDescription());
            if (StringUtils.isNotEmpty(taskRepresentation.getCategory())) {
                task.setCategory(taskRepresentation.getCategory());
            }

            task.setAssignee(identityService.getCurrentUserObject().getId().toString());
            this.taskService.saveTask(task);
            return new TaskRepresentation((Task) ((TaskQuery) this.taskService.createTaskQuery().taskId(task.getId())).singleResult());
        }
    }
}
