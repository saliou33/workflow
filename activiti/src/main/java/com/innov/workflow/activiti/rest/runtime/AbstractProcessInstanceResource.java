package com.innov.workflow.activiti.rest.runtime;

import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.model.runtime.ProcessInstanceRepresentation;
import com.innov.workflow.activiti.service.exception.NotFoundException;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import com.innov.workflow.activiti.service.runtime.ProcessInstanceService;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.api.FormService;
import org.activiti.form.model.FormDefinition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public abstract class AbstractProcessInstanceResource {
    private static final Logger logger = LoggerFactory.getLogger(AbstractProcessInstanceResource.class);
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected FormRepositoryService formRepositoryService;
    @Autowired
    protected FormService formService;
    @Autowired
    protected PermissionService permissionService;
    @Autowired
    protected ProcessInstanceService processInstanceService;

    @Autowired
    protected IdentityService identityService;

    public AbstractProcessInstanceResource() {
    }

    public ProcessInstanceRepresentation getProcessInstance(String processInstanceId, HttpServletResponse response) {
        HistoricProcessInstance processInstance = this.historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (!this.permissionService.hasReadPermissionOnProcessInstance(identityService.getCurrentUserObject(), processInstance, processInstanceId)) {
            throw new NotFoundException("Process with id: " + processInstanceId + " does not exist or is not available for this user");
        } else {
            ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) this.repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
            User userRep = null;
            if (processInstance.getStartUserId() != null) {
                userRep = this.identityService.getUser(processInstance.getStartUserId());

            }

            ProcessInstanceRepresentation processInstanceResult = new ProcessInstanceRepresentation(processInstance, processDefinition, processDefinition.isGraphicalNotationDefined(), userRep);
            FormDefinition formDefinition = this.getStartFormDefinition(processInstance.getProcessDefinitionId(), processDefinition, processInstance.getId());
            if (formDefinition != null) {
                processInstanceResult.setStartFormDefined(true);
            }

            return processInstanceResult;
        }
    }

    public FormDefinition getProcessInstanceStartForm(String processInstanceId, HttpServletResponse response) {
        HistoricProcessInstance processInstance = this.historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (!this.permissionService.hasReadPermissionOnProcessInstance(identityService.getCurrentUserObject(), processInstance, processInstanceId)) {
            throw new NotFoundException("Process with id: " + processInstanceId + " does not exist or is not available for this user");
        } else {
            ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) this.repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
            return this.getStartFormDefinition(processInstance.getProcessDefinitionId(), processDefinition, processInstance.getId());
        }
    }

    public void deleteProcessInstance(String processInstanceId) {
        User currentUser = identityService.getCurrentUserObject();
        HistoricProcessInstance processInstance = this.historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).startedBy(String.valueOf(currentUser.getId())).singleResult();
        if (processInstance == null) {
            throw new NotFoundException("Process with id: " + processInstanceId + " does not exist or is not started by this user");
        } else {
            if (processInstance.getEndTime() != null) {
                if (!this.permissionService.canDeleteProcessInstance(currentUser, processInstance)) {
                    throw new NotFoundException("Process with id: " + processInstanceId + " is already completed and can't be deleted");
                }

                this.processInstanceService.deleteProcessInstance(processInstanceId);
            } else {
                this.runtimeService.deleteProcessInstance(processInstanceId, "Cancelled by " + identityService.getCurrentUserObject().getId());
            }

        }
    }

    protected FormDefinition getStartFormDefinition(String processDefinitionId, ProcessDefinitionEntity processDefinition, String processInstanceId) {
        FormDefinition formDefinition = null;
        BpmnModel bpmnModel = this.repositoryService.getBpmnModel(processDefinitionId);
        Process process = bpmnModel.getProcessById(processDefinition.getKey());
        FlowElement startElement = process.getInitialFlowElement();
        if (startElement instanceof StartEvent) {
            StartEvent startEvent = (StartEvent) startElement;
            if (StringUtils.isNotEmpty(startEvent.getFormKey())) {
                formDefinition = this.formService.getCompletedTaskFormDefinitionByKeyAndParentDeploymentId(startEvent.getFormKey(), processDefinition.getDeploymentId(), null, processInstanceId, null, processDefinition.getTenantId());
            }
        }

        return formDefinition;
    }
}
