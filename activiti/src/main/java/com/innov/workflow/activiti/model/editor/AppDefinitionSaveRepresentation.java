package com.innov.workflow.activiti.model.editor;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;

@Data
public class AppDefinitionSaveRepresentation extends AbstractRepresentation {
    protected AppDefinitionRepresentation appDefinition;
    protected boolean publish;
    protected Boolean force;

}
