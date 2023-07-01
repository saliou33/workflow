package com.innov.workflow.activiti.rest.runtime;

import com.innov.workflow.activiti.model.runtime.TaskRepresentation;
import com.innov.workflow.activiti.model.runtime.TaskUpdateRepresentation;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")

public class TaskResource extends AbstractTaskResource {
    private final Logger log = LoggerFactory.getLogger(TaskResource.class);
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected PermissionService permissionService;
    @Autowired
    protected RepositoryService repositoryService;

    public TaskResource() {
    }

    @RequestMapping(
            value = {"/activiti/tasks/{taskId}"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public TaskRepresentation getTask(@PathVariable String taskId, HttpServletResponse response) {
        return super.getTask(taskId, response);
    }

    @RequestMapping(
            value = {"/activiti/tasks/{taskId}"},
            method = {RequestMethod.PUT},
            produces = {"application/json"}
    )
    public TaskRepresentation updateTask(@PathVariable("taskId") String taskId, @RequestBody TaskUpdateRepresentation updated) {
        return super.updateTask(taskId, updated);
    }
}
