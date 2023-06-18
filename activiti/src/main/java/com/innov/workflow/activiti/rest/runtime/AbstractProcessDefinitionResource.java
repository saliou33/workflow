package com.innov.workflow.activiti.rest.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.activiti.service.exception.NotFoundException;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.model.FormDefinition;
import org.activiti.form.model.FormField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;


@Service
public abstract class AbstractProcessDefinitionResource {
    private final Logger logger = LoggerFactory.getLogger(AbstractProcessDefinitionResource.class);
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected FormRepositoryService formRepositoryService;
    @Autowired
    protected PermissionService permissionService;
    @Autowired
    protected ObjectMapper objectMapper;

    public AbstractProcessDefinitionResource() {
    }

    public FormDefinition getProcessDefinitionStartForm(HttpServletRequest request) {
        String[] requestInfoArray = this.parseRequest(request);
        String processDefinitionId = this.getProcessDefinitionId(requestInfoArray, requestInfoArray.length - 2);
        ProcessDefinition processDefinition = this.permissionService.getProcessDefinitionById(processDefinitionId);

        try {
            return this.getStartForm(processDefinition);
        } catch (ActivitiObjectNotFoundException var6) {
            throw new NotFoundException("No process definition found with the given id: " + processDefinitionId);
        }
    }

    protected FormDefinition getStartForm(ProcessDefinition processDefinition) {
        FormDefinition formDefinition = null;
        BpmnModel bpmnModel = this.repositoryService.getBpmnModel(processDefinition.getId());
        Process process = bpmnModel.getProcessById(processDefinition.getKey());
        FlowElement startElement = process.getInitialFlowElement();
        if (startElement instanceof StartEvent) {
            StartEvent startEvent = (StartEvent) startElement;
            if (StringUtils.isNotEmpty(startEvent.getFormKey())) {
                formDefinition = this.formRepositoryService.getFormDefinitionByKeyAndParentDeploymentId(startEvent.getFormKey(), processDefinition.getDeploymentId(), processDefinition.getTenantId());
            }
        }

        if (formDefinition == null) {
            throw new NotFoundException("Process definition does not have a form defined: " + processDefinition.getId());
        } else {
            return formDefinition;
        }
    }

    protected ProcessDefinition getProcessDefinitionFromRequest(String[] requestInfoArray, boolean isTableRequest) {
        int paramPosition = requestInfoArray.length - 3;
        if (isTableRequest) {
            --paramPosition;
        }

        String processDefinitionId = this.getProcessDefinitionId(requestInfoArray, paramPosition);
        ProcessDefinition processDefinition = this.permissionService.getProcessDefinitionById(processDefinitionId);
        return processDefinition;
    }

    protected FormField getFormFieldFromRequest(String[] requestInfoArray, ProcessDefinition processDefinition, boolean isTableRequest) {
        FormDefinition form = this.getStartForm(processDefinition);
        int paramPosition = requestInfoArray.length - 1;
        if (isTableRequest) {
            --paramPosition;
        }

        String fieldVariable = requestInfoArray[paramPosition];
        List<? extends FormField> allFields = form.listAllFields();
        FormField selectedField = null;
        if (CollectionUtils.isNotEmpty(allFields)) {
            Iterator i$ = allFields.iterator();

            while (i$.hasNext()) {
                FormField formFieldRepresentation = (FormField) i$.next();
                if (formFieldRepresentation.getId().equalsIgnoreCase(fieldVariable)) {
                    selectedField = formFieldRepresentation;
                }
            }
        }

        if (selectedField == null) {
            throw new NotFoundException("Field could not be found in start form definition " + fieldVariable);
        } else {
            return selectedField;
        }
    }

    protected String[] parseRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] requestInfoArray = requestURI.split("/");
        if (requestInfoArray.length < 2) {
            throw new BadRequestException("Start form request is not valid " + requestURI);
        } else {
            return requestInfoArray;
        }
    }

    protected String getProcessDefinitionId(String[] requestInfoArray, int position) {
        String processDefinitionVariable = requestInfoArray[position];
        String processDefinitionId = null;

        try {
            processDefinitionId = URLDecoder.decode(processDefinitionVariable, "UTF-8");
            return processDefinitionId;
        } catch (Exception var6) {
            this.logger.error("Error decoding process definition " + processDefinitionVariable, var6);
            throw new InternalServerErrorException("Error decoding process definition " + processDefinitionVariable);
        }
    }
}
