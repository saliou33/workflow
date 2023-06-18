package com.innov.workflow.activiti.model.editor;

import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.domain.editor.AppDefinition;
import lombok.Data;


@Data

public class AppDefinitionListModelRepresentation extends ModelRepresentation {
    protected AppDefinition appDefinition;

    public AppDefinitionListModelRepresentation(AbstractModel model) {
        this.initialize(model);
    }

    public AppDefinitionListModelRepresentation() {
    }
}
