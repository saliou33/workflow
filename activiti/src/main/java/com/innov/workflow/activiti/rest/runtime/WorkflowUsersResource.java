package com.innov.workflow.activiti.rest.runtime;

import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")

public class WorkflowUsersResource extends AbstractWorkflowUsersResource {
    public WorkflowUsersResource() {
    }

    @RequestMapping(
            value = {"/rest/workflow-users"},
            method = {RequestMethod.GET}
    )
    public ResultListDataRepresentation getUsers(@RequestParam(value = "filter", required = false) String filter, @RequestParam(value = "email", required = false) String email, @RequestParam(value = "externalId", required = false) String externalId, @RequestParam(value = "excludeTaskId", required = false) String excludeTaskId, @RequestParam(value = "excludeProcessId", required = false) String excludeProcessId, @RequestParam(value = "groupId", required = false) Long groupId, @RequestParam(value = "tenantId", required = false) Long tenantId) {
        return super.getUsers(filter, email, excludeTaskId, excludeProcessId, groupId);
    }
}

