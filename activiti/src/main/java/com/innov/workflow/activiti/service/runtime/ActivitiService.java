package com.innov.workflow.activiti.service.runtime;

import com.innov.workflow.activiti.custom.service.IdentityService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLinkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ActivitiService {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private IdentityService identityService;

    public ActivitiService() {
    }

    public ProcessInstance startProcessInstance(String processDefinitionId, Map<String, Object> variables, String processInstanceName) {
        ProcessInstance processInstance = this.runtimeService.startProcessInstanceById(processDefinitionId, variables);

        if (!processInstance.isEnded() && processInstanceName != null) {
            this.runtimeService.setProcessInstanceName(processInstance.getId(), processInstanceName);
        }

        List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstance.getId()).list();

        for (HistoricTaskInstance task : taskList) {
            if (task.getAssignee() == null) {
                taskService.addUserIdentityLink(task.getId(), identityService.getCurrentUserObject().getId(), IdentityLinkType.ASSIGNEE);
            }
        }

        return processInstance;
    }
}
