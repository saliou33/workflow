package com.innov.workflow.activiti.service.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.model.runtime.CompleteFormRepresentation;
import com.innov.workflow.activiti.model.runtime.ProcessInstanceVariableRepresentation;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.service.exception.NotFoundException;
import com.innov.workflow.activiti.service.exception.NotPermittedException;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.api.FormService;
import org.activiti.form.model.FormDefinition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

;

@Service
public class ActivitiTaskFormService {
    private static final Logger logger = LoggerFactory.getLogger(ActivitiTaskFormService.class);
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected FormRepositoryService formRepositoryService;
    @Autowired
    protected FormService formService;
    @Autowired
    protected PermissionService permissionService;
    @Autowired
    protected IdentityService identityService;

    protected ObjectMapper objectMapper;

    public ActivitiTaskFormService() {
    }

    public FormDefinition getTaskForm(String taskId) {
        HistoricTaskInstance task = this.permissionService.validateReadPermissionOnTask(identityService.getCurrentUserObject(), taskId);
        Map<String, Object> variables = new HashMap();
        Iterator formDefinition;
        if (task.getProcessInstanceId() != null) {
            List<HistoricVariableInstance> variableInstances = this.historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
            formDefinition = variableInstances.iterator();

            while (formDefinition.hasNext()) {
                HistoricVariableInstance historicVariableInstance = (HistoricVariableInstance) formDefinition.next();
                variables.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
            }
        }

        String parentDeploymentId = null;
        if (StringUtils.isNotEmpty(task.getProcessDefinitionId())) {
            try {
                ProcessDefinition processDefinition = this.repositoryService.getProcessDefinition(task.getProcessDefinitionId());
                parentDeploymentId = processDefinition.getDeploymentId();
            } catch (ActivitiException var7) {
                logger.error("Error getting process definition " + task.getProcessDefinitionId(), var7);
            }
        }

        Object obj;
        if (task.getEndTime() != null) {
            obj = this.formService.getCompletedTaskFormDefinitionByKeyAndParentDeploymentId(task.getFormKey(), parentDeploymentId, taskId, task.getProcessInstanceId(), variables, task.getTenantId());
        } else {
            obj = this.formService.getTaskFormDefinitionByKeyAndParentDeploymentId(task.getFormKey(), parentDeploymentId, task.getProcessInstanceId(), variables, task.getTenantId());
        }

        if (obj == null) {
            throw new NotFoundException("Form definition for task " + task.getTaskDefinitionKey() + " cannot be found for form key " + task.getFormKey());
        } else {
            return (FormDefinition) obj;
        }
    }

    public void completeTaskForm(String taskId, CompleteFormRepresentation completeTaskFormRepresentation) {
        Task task = (Task) ((TaskQuery) this.taskService.createTaskQuery().taskId(taskId)).singleResult();
        if (task == null) {
            throw new NotFoundException("Task not found with id: " + taskId);
        } else {
            FormDefinition formDefinition = this.formRepositoryService.getFormDefinitionById(completeTaskFormRepresentation.getFormId());
            User currentUser = identityService.getCurrentUserObject();
            if (!this.permissionService.isTaskOwnerOrAssignee(currentUser, taskId) && !this.permissionService.validateIfUserIsInitiatorAndCanCompleteTask(currentUser, task)) {
                throw new NotPermittedException();
            } else {
                Map<String, Object> variables = this.formService.getVariablesFromFormSubmission(formDefinition, completeTaskFormRepresentation.getValues(), completeTaskFormRepresentation.getOutcome());
                this.formService.storeSubmittedForm(variables, formDefinition, task.getId(), task.getProcessInstanceId());
                this.taskService.complete(taskId, variables);
            }
        }
    }

    public List<ProcessInstanceVariableRepresentation> getProcessInstanceVariables(String taskId) {
        HistoricTaskInstance task = this.permissionService.validateReadPermissionOnTask(identityService.getCurrentUserObject(), taskId);
        List<HistoricVariableInstance> historicVariables = this.historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
        Map<String, ProcessInstanceVariableRepresentation> processInstanceVariables = new HashMap();
        Iterator i$ = historicVariables.iterator();

        while (i$.hasNext()) {
            HistoricVariableInstance historicVariableInstance = (HistoricVariableInstance) i$.next();
            ProcessInstanceVariableRepresentation processInstanceVariableRepresentation = new ProcessInstanceVariableRepresentation(historicVariableInstance.getVariableName(), historicVariableInstance.getVariableTypeName(), historicVariableInstance.getValue());
            processInstanceVariables.put(historicVariableInstance.getId(), processInstanceVariableRepresentation);
        }


        List<ProcessInstanceVariableRepresentation> processInstanceVariableRepresenations = new ArrayList(processInstanceVariables.values());
        return processInstanceVariableRepresenations;
    }


}
