package com.innov.workflow.activiti.rest.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.idm.UserRepresentation;
import com.innov.workflow.activiti.model.runtime.TaskRepresentation;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.NotPermittedException;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class HistoricTaskQueryResource {
    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected PermissionService permissionService;
    @Autowired
    protected IdentityService identityService;

    public HistoricTaskQueryResource() {
    }

    @RequestMapping(
            value = {"/activiti/query/history/tasks"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public ResultListDataRepresentation listTasks(@RequestBody ObjectNode requestNode) {
        if (requestNode == null) {
            throw new BadRequestException("No request found");
        } else {
            HistoricTaskInstanceQuery taskQuery = this.historyService.createHistoricTaskInstanceQuery();
            User currentUser = identityService.getCurrentUserObject();
            JsonNode processInstanceIdNode = requestNode.get("processInstanceId");
            if (processInstanceIdNode != null && !processInstanceIdNode.isNull()) {
                String processInstanceId = processInstanceIdNode.asText();
                if (!this.permissionService.hasReadPermissionOnProcessInstance(currentUser, processInstanceId)) {
                    throw new NotPermittedException();
                }

                taskQuery.processInstanceId(processInstanceId);
            }

            JsonNode finishedNode = requestNode.get("finished");
            if (finishedNode != null && !finishedNode.isNull()) {
                boolean isFinished = finishedNode.asBoolean();
                if (isFinished) {
                    taskQuery.finished();
                } else {
                    taskQuery.unfinished();
                }
            }

            List<HistoricTaskInstance> tasks = taskQuery.list();
            ResultListDataRepresentation result = new ResultListDataRepresentation(this.convertTaskInfoList(tasks));
            return result;
        }
    }

    protected List<TaskRepresentation> convertTaskInfoList(List<HistoricTaskInstance> tasks) {
        List<TaskRepresentation> result = new ArrayList();
        if (CollectionUtils.isNotEmpty(tasks)) {
            TaskRepresentation representation = null;

            for (Iterator i$ = tasks.iterator(); i$.hasNext(); result.add(representation)) {
                HistoricTaskInstance task = (HistoricTaskInstance) i$.next();
                representation = new TaskRepresentation(task);
                User user = this.identityService.getUser(task.getAssignee());
                if (user != null) {
                    representation.setAssignee(new UserRepresentation(user));
                }
            }
        }

        return result;
    }
}
