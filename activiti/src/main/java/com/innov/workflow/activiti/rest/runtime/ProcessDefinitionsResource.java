package com.innov.workflow.activiti.rest.runtime;

import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")

public class ProcessDefinitionsResource extends AbstractProcessDefinitionsResource {
    public ProcessDefinitionsResource() {
    }

    @RequestMapping(
            value = {"/activiti/process-definitions"},
            method = {RequestMethod.GET}
    )
    public ResultListDataRepresentation getProcessDefinitions(@RequestParam(value = "latest", required = false) Boolean latest, @RequestParam(value = "deploymentKey", required = false) String deploymentKey) {
        return super.getProcessDefinitions(latest, deploymentKey);
    }
}
