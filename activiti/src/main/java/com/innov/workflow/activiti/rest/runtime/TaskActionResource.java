package com.innov.workflow.activiti.rest.runtime;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.model.runtime.TaskRepresentation;
import com.innov.workflow.activiti.service.editor.ActivitiTaskActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")

public class TaskActionResource {
    @Autowired
    protected ActivitiTaskActionService taskActionService;

    public TaskActionResource() {
    }

    @RequestMapping(
            value = {"/rest/tasks/{taskId}/action/complete"},
            method = {RequestMethod.PUT}
    )
    @ResponseStatus(HttpStatus.OK)
    public void completeTask(@PathVariable String taskId) {
        this.taskActionService.completeTask(taskId);
    }

    @RequestMapping(
            value = {"/rest/tasks/{taskId}/action/assign"},
            method = {RequestMethod.PUT}
    )
    public TaskRepresentation assignTask(@PathVariable String taskId, @RequestBody ObjectNode requestNode) {
        return this.taskActionService.assignTask(taskId, requestNode);
    }

    @RequestMapping(
            value = {"/rest/tasks/{taskId}/action/involve"},
            method = {RequestMethod.PUT},
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public void involveUser(@PathVariable("taskId") String taskId, @RequestBody ObjectNode requestNode) {
        this.taskActionService.involveUser(taskId, requestNode);
    }

    @RequestMapping(
            value = {"/rest/tasks/{taskId}/action/remove-involved"},
            method = {RequestMethod.PUT},
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public void removeInvolvedUser(@PathVariable("taskId") String taskId, @RequestBody ObjectNode requestNode) {
        this.taskActionService.removeInvolvedUser(taskId, requestNode);
    }

    @RequestMapping(
            value = {"/rest/tasks/{taskId}/action/claim"},
            method = {RequestMethod.PUT}
    )
    @ResponseStatus(HttpStatus.OK)
    public void claimTask(@PathVariable String taskId) {
        this.taskActionService.claimTask(taskId);
    }
}
