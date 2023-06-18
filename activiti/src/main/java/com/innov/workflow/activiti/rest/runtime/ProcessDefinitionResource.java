package com.innov.workflow.activiti.rest.runtime;

import org.activiti.form.model.FormDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/app")

public class ProcessDefinitionResource extends AbstractProcessDefinitionResource {
    public ProcessDefinitionResource() {
    }

    @RequestMapping(
            value = {"/rest/process-definitions/{processDefinitionId}/start-form"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public FormDefinition getProcessDefinitionStartForm(HttpServletRequest request) {
        return super.getProcessDefinitionStartForm(request);
    }
}
