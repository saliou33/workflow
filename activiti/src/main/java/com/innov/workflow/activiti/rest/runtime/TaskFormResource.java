package com.innov.workflow.activiti.rest.runtime;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.innov.workflow.activiti.model.runtime.CompleteFormRepresentation;
import com.innov.workflow.activiti.model.runtime.ProcessInstanceVariableRepresentation;
import com.innov.workflow.activiti.service.editor.ActivitiTaskFormService;
import org.activiti.form.model.FormDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/app/rest/task-forms"})
public class TaskFormResource {
    @Autowired
    protected ActivitiTaskFormService taskFormService;

    public TaskFormResource() {
    }

    @RequestMapping(
            value = {"/{taskId}"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public FormDefinition getTaskForm(@PathVariable String taskId) {
        return this.taskFormService.getTaskForm(taskId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(
            value = {"/{taskId}"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public void completeTaskForm(@PathVariable String taskId, @RequestBody CompleteFormRepresentation completeTaskFormRepresentation) {
        this.taskFormService.completeTaskForm(taskId, completeTaskFormRepresentation);
    }

    @RequestMapping(
            value = {"/{taskId}/variables"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public List<ProcessInstanceVariableRepresentation> getProcessInstanceVariables(@PathVariable String taskId) {
        return this.taskFormService.getProcessInstanceVariables(taskId);
    }
}
