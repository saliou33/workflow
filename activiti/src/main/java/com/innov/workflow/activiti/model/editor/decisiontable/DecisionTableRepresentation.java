package com.innov.workflow.activiti.model.editor.decisiontable;

import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;

import java.util.Date;

@Data
public class DecisionTableRepresentation extends AbstractRepresentation {
    protected String id;
    protected String name;
    protected String key;
    protected String description;
    protected Integer version;
    protected String lastUpdatedBy;
    protected Date lastUpdated;
    protected DecisionTableDefinitionRepresentation decisionTableDefinition;

    public DecisionTableRepresentation(AbstractModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.key = model.getKey();
        this.description = model.getDescription();
        this.version = model.getVersion();
        this.lastUpdated = model.getLastUpdated();
        this.lastUpdatedBy = model.getLastUpdatedBy();
    }
}
