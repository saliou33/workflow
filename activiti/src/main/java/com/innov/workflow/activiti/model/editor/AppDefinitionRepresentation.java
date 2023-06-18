package com.innov.workflow.activiti.model.editor;

import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.domain.editor.AppDefinition;
import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class AppDefinitionRepresentation extends AbstractRepresentation {
    private String id;
    private String name;
    private String key;
    private String description;
    private Integer version;
    private Date created;
    private AppDefinition definition;

    public AppDefinitionRepresentation(AbstractModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.key = model.getKey();
        this.description = model.getDescription();
        this.version = model.getVersion();
        this.created = model.getCreated();
    }
}
