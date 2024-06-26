package com.innov.workflow.activiti.rest.runtime;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")

public class TaskQueryResource extends AbstractTaskQueryResource {
    public TaskQueryResource() {
    }

    @RequestMapping(
            value = {"/activiti/query/tasks"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public ResultListDataRepresentation listTasks(@RequestBody ObjectNode requestNode) {
        return super.listTasks(requestNode);
    }

    @RequestMapping(
            value = {"/activiti/query/tasks/count"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public Map countTasks() {
        return super.countTasks();
    }
}
