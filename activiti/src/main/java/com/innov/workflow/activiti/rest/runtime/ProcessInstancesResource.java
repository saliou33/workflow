package com.innov.workflow.activiti.rest.runtime;

import com.innov.workflow.activiti.model.runtime.CreateProcessInstanceRepresentation;
import com.innov.workflow.activiti.model.runtime.ProcessInstanceRepresentation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class ProcessInstancesResource extends AbstractProcessInstancesResource {
    public ProcessInstancesResource() {
    }

    @RequestMapping(
            value = {"/rest/process-instances"},
            method = {RequestMethod.POST}
    )
    public ProcessInstanceRepresentation startNewProcessInstance(@RequestBody CreateProcessInstanceRepresentation startRequest) {
        return super.startNewProcessInstance(startRequest);
    }
}
