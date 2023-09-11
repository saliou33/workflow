package com.innov.workflow.activiti.rest.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.custom.dao.ActFormDefinitionService;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.domain.runtime.RelatedContent;
import com.innov.workflow.activiti.model.component.SimpleContentTypeMapper;
import com.innov.workflow.activiti.model.runtime.CreateProcessInstanceRepresentation;
import com.innov.workflow.activiti.model.runtime.ProcessInstanceRepresentation;
import com.innov.workflow.activiti.model.runtime.RelatedContentRepresentation;
import com.innov.workflow.activiti.repository.editor.ModelRepository;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.runtime.ActivitiService;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import com.innov.workflow.activiti.service.runtime.RelatedContentService;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.api.FormService;
import org.activiti.form.model.FormDefinition;
import org.activiti.form.model.FormField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public abstract class AbstractProcessInstancesResource {
    @Autowired
    protected ActivitiService activitiService;
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
    protected RelatedContentService relatedContentService;
    @Autowired
    protected SimpleContentTypeMapper typeMapper;
    @Autowired
    protected IdentityService identityService;
    @Autowired
    protected ModelRepository modelRepository;

    @Autowired
    protected ActFormDefinitionService formDefinitionService;

    @Autowired
    protected ObjectMapper objectMapper;

    public AbstractProcessInstancesResource() {

    }

    public ProcessInstanceRepresentation startNewProcessInstance(CreateProcessInstanceRepresentation startRequest) {
        if (StringUtils.isEmpty(startRequest.getProcessDefinitionId())) {
            throw new BadRequestException("Process definition id is required");
        } else {
            FormDefinition formDefinition = null;
            Map<String, Object> variables = null;
            ProcessDefinition processDefinition = this.permissionService.getProcessDefinitionById(startRequest.getProcessDefinitionId());
            if (startRequest.getValues() != null || startRequest.getOutcome() != null) {
                BpmnModel bpmnModel = this.repositoryService.getBpmnModel(processDefinition.getId());
                Process process = bpmnModel.getProcessById(processDefinition.getKey());

                FlowElement startElement = process.getInitialFlowElement();
                if (startElement instanceof StartEvent) {
                    StartEvent startEvent = (StartEvent) startElement;
                    if (StringUtils.isNotEmpty(startEvent.getFormKey())) {
                        formDefinition = this.formRepositoryService.getFormDefinitionByKeyAndParentDeploymentId(startEvent.getFormKey(), processDefinition.getDeploymentId());

                        if (formDefinition != null) {
                            variables = this.formService.getVariablesFromFormSubmission(formDefinition, startRequest.getValues(), startRequest.getOutcome());
                        }
                    }
                }
            }

            RelatedContent relatedContent = null;
            if (formDefinition != null) {
                for (FormField formField : formDefinition.getFields()) {
                    if (formField.getType().equals("upload")) {
                        Long relatedContendId = Long.valueOf((String) startRequest.getValues().get(formField.getId()));
                        relatedContent = relatedContentService.getRelatedContent(relatedContendId, false);
                    }
                }
            }

            ProcessInstance processInstance = this.activitiService.startProcessInstance(startRequest.getProcessDefinitionId(), variables, startRequest.getName());

            System.out.println(variables);
            if (relatedContent != null) {
                relatedContent.setProcessInstanceId(processInstance.getProcessInstanceId());
                relatedContentService.storeRelatedContent(relatedContent);
            }

            HistoricProcessInstance historicProcess = this.historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
            if (formDefinition != null) {
                this.formService.storeSubmittedForm(variables, formDefinition, null, historicProcess.getId());
            }

            User user = null;
            if (historicProcess.getStartUserId() != null) {
                user = identityService.getUser(historicProcess.getStartUserId());
            }

            return new ProcessInstanceRepresentation(historicProcess, processDefinition, ((ProcessDefinitionEntity) processDefinition).isGraphicalNotationDefined(), user);
        }
    }


    protected Map<String, List<RelatedContent>> groupContentByField(Page<RelatedContent> allContent) {
        HashMap<String, List<RelatedContent>> result = new HashMap();

        Object list;
        RelatedContent content;
        for (Iterator i$ = allContent.getContent().iterator(); i$.hasNext(); ((List) list).add(content)) {
            content = (RelatedContent) i$.next();
            list = result.get(content.getField());
            if (list == null) {
                list = new ArrayList();
                result.put(content.getField(), (List<RelatedContent>) list);
            }
        }

        return result;
    }

    protected RelatedContentRepresentation createRelatedContentResponse(RelatedContent relatedContent) {
        RelatedContentRepresentation relatedContentResponse = new RelatedContentRepresentation(relatedContent, this.typeMapper);
        return relatedContentResponse;
    }
}
