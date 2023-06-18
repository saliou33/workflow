package com.innov.workflow.activiti.model.runtime;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import com.innov.workflow.activiti.model.idm.UserRepresentation;
import com.innov.workflow.core.domain.entity.User;
import lombok.Data;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ProcessInstanceRepresentation extends AbstractRepresentation {
    protected String id;
    protected String name;
    protected String businessKey;
    protected String processDefinitionId;
    protected String tenantId;
    protected Date started;
    protected Date ended;
    protected UserRepresentation startedBy;
    protected String processDefinitionName;
    protected String processDefinitionDescription;
    protected String processDefinitionKey;
    protected String processDefinitionCategory;
    protected int processDefinitionVersion;
    protected String processDefinitionDeploymentId;
    protected boolean graphicalNotationDefined;
    protected boolean startFormDefined;
    protected List<RestVariable> variables;

    public ProcessInstanceRepresentation(ProcessInstance processInstance, ProcessDefinition processDefinition, boolean graphicalNotation, User startedBy) {
        this(processInstance, graphicalNotation, startedBy);
        this.mapProcessDefinition(processDefinition);
    }

    public ProcessInstanceRepresentation(ProcessInstance processInstance, boolean graphicalNotation, User startedBy) {
        this.variables = new ArrayList();
        this.id = processInstance.getId();
        this.name = processInstance.getName();
        this.businessKey = processInstance.getBusinessKey();
        this.processDefinitionId = processInstance.getProcessDefinitionId();
        this.tenantId = processInstance.getTenantId();
        this.graphicalNotationDefined = graphicalNotation;
        this.startedBy = new UserRepresentation(startedBy);
    }

    public ProcessInstanceRepresentation(HistoricProcessInstance processInstance, ProcessDefinition processDefinition, boolean graphicalNotation, User startedBy) {
        this(processInstance, graphicalNotation, startedBy);
        this.mapProcessDefinition(processDefinition);
    }

    public ProcessInstanceRepresentation(HistoricProcessInstance processInstance, boolean graphicalNotation, User startedBy) {
        this.variables = new ArrayList();
        this.id = processInstance.getId();
        this.name = processInstance.getName();
        this.businessKey = processInstance.getBusinessKey();
        this.processDefinitionId = processInstance.getProcessDefinitionId();
        this.tenantId = processInstance.getTenantId();
        this.graphicalNotationDefined = graphicalNotation;
        this.started = processInstance.getStartTime();
        this.ended = processInstance.getEndTime();
        this.startedBy = new UserRepresentation(startedBy);
    }

    public ProcessInstanceRepresentation() {
        this.variables = new ArrayList();
    }

    protected void mapProcessDefinition(ProcessDefinition processDefinition) {
        if (processDefinition != null) {
            this.processDefinitionName = processDefinition.getName();
            this.processDefinitionDescription = processDefinition.getDescription();
            this.processDefinitionKey = processDefinition.getKey();
            this.processDefinitionCategory = processDefinition.getCategory();
            this.processDefinitionVersion = processDefinition.getVersion();
            this.processDefinitionDeploymentId = processDefinition.getDeploymentId();
        }

    }

}
