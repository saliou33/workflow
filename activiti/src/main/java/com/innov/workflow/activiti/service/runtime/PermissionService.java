package com.innov.workflow.activiti.service.runtime;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.innov.workflow.activiti.domain.runtime.RelatedContent;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.service.exception.NotFoundException;
import com.innov.workflow.activiti.service.exception.NotPermittedException;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected IdentityService identityService;

    public PermissionService() {
    }

    public HistoricTaskInstance validateReadPermissionOnTask(User user, String taskId) {
        List<HistoricTaskInstance> tasks = this.historyService.createHistoricTaskInstanceQuery().taskId(taskId).taskInvolvedUser(String.valueOf(user.getId())).list();
        if (CollectionUtils.isNotEmpty(tasks)) {
            return tasks.get(0);
        } else {
            HistoricTaskInstanceQuery historicTaskInstanceQuery = this.historyService.createHistoricTaskInstanceQuery();
            historicTaskInstanceQuery.taskId(taskId);
            List<String> groupIds = this.getGroupIdsForUser(user);
            if (!groupIds.isEmpty()) {
                historicTaskInstanceQuery.taskCandidateGroupIn(this.getGroupIdsForUser(user));
            }

            tasks = historicTaskInstanceQuery.list();
            if (CollectionUtils.isNotEmpty(tasks)) {
                return tasks.get(0);
            } else {
                tasks = this.historyService.createHistoricTaskInstanceQuery().taskId(taskId).list();
                if (CollectionUtils.isNotEmpty(tasks)) {
                    HistoricTaskInstance task = tasks.get(0);
                    if (task != null && task.getProcessInstanceId() != null) {
                        boolean hasReadPermissionOnProcessInstance = this.hasReadPermissionOnProcessInstance(user, task.getProcessInstanceId());
                        if (hasReadPermissionOnProcessInstance) {
                            return task;
                        }
                    }
                }

                throw new NotPermittedException("User is not allowed to work with task " + taskId);
            }
        }
    }

    private List<String> getGroupIdsForUser(User user) {

        return identityService.getGroupsIds(user);
    }

    public boolean isTaskOwnerOrAssignee(User user, String taskId) {
        return this.isTaskOwnerOrAssignee(user, this.taskService.createTaskQuery().taskId(taskId).singleResult());
    }

    public boolean isTaskOwnerOrAssignee(User user, Task task) {
        String currentUser = String.valueOf(user.getId());
        return currentUser.equals(task.getAssignee()) || currentUser.equals(task.getOwner());
    }

    public boolean validateIfUserIsInitiatorAndCanCompleteTask(User user, Task task) {
        boolean canCompleteTask = false;
        if (task.getProcessInstanceId() != null) {
            HistoricProcessInstance historicProcessInstance = this.historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            if (historicProcessInstance != null && StringUtils.isNotEmpty(historicProcessInstance.getStartUserId())) {
                String processInstanceStartUserId = historicProcessInstance.getStartUserId();
                if (String.valueOf(user.getId()).equals(processInstanceStartUserId)) {
                    BpmnModel bpmnModel = this.repositoryService.getBpmnModel(task.getProcessDefinitionId());
                    FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
                    if (flowElement != null && flowElement instanceof UserTask) {
                        UserTask userTask = (UserTask) flowElement;
                        List<ExtensionElement> extensionElements = userTask.getExtensionElements().get("initiator-can-complete");
                        if (CollectionUtils.isNotEmpty(extensionElements)) {
                            String value = extensionElements.get(0).getElementText();
                            if (StringUtils.isNotEmpty(value) && Boolean.valueOf(value)) {
                                canCompleteTask = true;
                            }
                        }
                    }
                }
            }
        }

        return canCompleteTask;
    }

    public boolean isInvolved(User user, String taskId) {
        return this.historyService.createHistoricTaskInstanceQuery().taskId(taskId).taskInvolvedUser(String.valueOf(user.getId())).count() == 1L;
    }

    public boolean hasReadPermissionOnProcessInstance(User user, String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = this.historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        return this.hasReadPermissionOnProcessInstance(user, historicProcessInstance, processInstanceId);
    }

    public boolean hasReadPermissionOnProcessInstance(User user, HistoricProcessInstance historicProcessInstance, String processInstanceId) {
        if (historicProcessInstance == null) {
            throw new NotFoundException("Process instance not found for id " + processInstanceId);
        } else if (historicProcessInstance.getStartUserId() != null && historicProcessInstance.getStartUserId().equals(user.getId())) {
            return true;
        } else {
            HistoricProcessInstanceQuery historicProcessInstanceQuery = this.historyService.createHistoricProcessInstanceQuery();
            historicProcessInstanceQuery.processInstanceId(processInstanceId);
            historicProcessInstanceQuery.involvedUser(user.getId());
            if (historicProcessInstanceQuery.count() > 0L) {
                return true;
            } else {
                HistoricTaskInstanceQuery historicTaskInstanceQuery = this.historyService.createHistoricTaskInstanceQuery();
                historicTaskInstanceQuery.processInstanceId(processInstanceId);
                historicTaskInstanceQuery.taskInvolvedUser(user.getId());
                if (historicTaskInstanceQuery.count() > 0L) {
                    return true;
                } else {
                    List<String> groupIds = this.getGroupIdsForUser(user);
                    if (!groupIds.isEmpty()) {
                        historicTaskInstanceQuery = this.historyService.createHistoricTaskInstanceQuery();
                        historicTaskInstanceQuery.processInstanceId(processInstanceId).taskCandidateGroupIn(groupIds);
                        return historicTaskInstanceQuery.count() > 0L;
                    } else {
                        return false;
                    }
                }
            }
        }
    }

    public boolean canAddRelatedContentToTask(User user, String taskId) {
        this.validateReadPermissionOnTask(user, taskId);
        return true;
    }

    public boolean canAddRelatedContentToProcessInstance(User user, String processInstanceId) {
        return this.hasReadPermissionOnProcessInstance(user, processInstanceId);
    }

    public boolean canDownloadContent(User currentUserObject, RelatedContent content) {
        if (content.getTaskId() != null) {
            this.validateReadPermissionOnTask(currentUserObject, content.getTaskId());
            return true;
        } else {
            return content.getProcessInstanceId() != null && this.hasReadPermissionOnProcessInstance(currentUserObject, content.getProcessInstanceId());
        }
    }

    public boolean hasWritePermissionOnRelatedContent(User user, RelatedContent content) {
        if (content.getProcessInstanceId() != null) {
            return this.hasReadPermissionOnProcessInstance(user, content.getProcessInstanceId());
        } else {
            return content.getCreatedBy() != null && content.getCreatedBy().equals(user.getId());
        }
    }

    public ProcessDefinition getProcessDefinitionById(String processDefinitionId) {
        return this.repositoryService.getProcessDefinition(processDefinitionId);
    }

    public boolean canDeleteProcessInstance(User currentUser, HistoricProcessInstance processInstance) {
        boolean canDelete = false;
        if (processInstance.getStartUserId() != null) {
            canDelete = processInstance.getStartUserId().equals(currentUser.getId());
        }

        return canDelete;
    }
}
