package com.innov.workflow.activiti.rest.runtime;


import com.innov.workflow.activiti.model.runtime.ProcessInstanceRepresentation;
import org.activiti.form.model.FormDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/app")
public class ProcessInstanceResource extends AbstractProcessInstanceResource {
    public ProcessInstanceResource() {
    }

    @RequestMapping(
            value = {"/rest/process-instances/{processInstanceId}"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public ProcessInstanceRepresentation getProcessInstance(@PathVariable String processInstanceId, HttpServletResponse response) {
        return super.getProcessInstance(processInstanceId, response);
    }

    @RequestMapping(
            value = {"/rest/process-instances/{processInstanceId}/start-form"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public FormDefinition getProcessInstanceStartForm(@PathVariable String processInstanceId, HttpServletResponse response) {
        return super.getProcessInstanceStartForm(processInstanceId, response);
    }

    @RequestMapping(
            value = {"/rest/process-instances/{processInstanceId}"},
            method = {RequestMethod.DELETE}
    )
    @ResponseStatus(HttpStatus.OK)
    public void deleteProcessInstance(@PathVariable String processInstanceId) {
        super.deleteProcessInstance(processInstanceId);
    }
}
