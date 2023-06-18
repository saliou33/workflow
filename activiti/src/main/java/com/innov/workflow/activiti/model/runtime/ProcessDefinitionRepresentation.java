package com.innov.workflow.activiti.model.runtime;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.activiti.engine.repository.ProcessDefinition;


@Data
@NoArgsConstructor
public class ProcessDefinitionRepresentation extends AbstractRepresentation {
    protected String id;
    protected String name;
    protected String description;
    protected String key;
    protected String category;
    protected int version;
    protected String deploymentId;
    protected String tenantId;
    protected boolean hasStartForm;

    public ProcessDefinitionRepresentation(ProcessDefinition processDefinition) {
        this.id = processDefinition.getId();
        this.name = processDefinition.getName();
        this.description = processDefinition.getDescription();
        this.key = processDefinition.getKey();
        this.category = processDefinition.getCategory();
        this.version = processDefinition.getVersion();
        this.deploymentId = processDefinition.getDeploymentId();
        this.tenantId = processDefinition.getTenantId();
        this.hasStartForm = processDefinition.hasStartFormKey();
    }

}
