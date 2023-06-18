package com.innov.workflow.activiti.model.editor.form;

import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.activiti.form.model.FormDefinition;

import java.util.Date;

@Data
@NoArgsConstructor
public class FormRepresentation extends AbstractRepresentation {
    protected String id;
    protected String name;
    protected String key;
    protected String description;
    protected Integer version;
    protected String lastUpdatedBy;
    protected Date lastUpdated;
    protected FormDefinition formDefinition;

    public FormRepresentation(AbstractModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.key = model.getKey();
        this.description = model.getDescription();
        this.version = model.getVersion();
        this.lastUpdated = model.getLastUpdated();
        this.lastUpdatedBy = model.getLastUpdatedBy();
    }
}
