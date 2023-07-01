package com.innov.workflow.activiti.rest.runtime;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProcessInstanceQueryResource extends AbstractProcessInstanceQueryResource {
    public ProcessInstanceQueryResource() {
    }

    @RequestMapping(
            value = {"/activiti/query/process-instances"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public ResultListDataRepresentation getProcessInstances(@RequestBody ObjectNode requestNode) {
        return super.getProcessInstances(requestNode);
    }
}
