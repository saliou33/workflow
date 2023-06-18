package com.innov.workflow.activiti.model.runtime;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;

import java.util.List;

public class RuntimeAppDefinitionSaveRepresentation extends AbstractRepresentation {
    private List<AppDefinitionRepresentation> appDefinitions;

    public RuntimeAppDefinitionSaveRepresentation() {
    }

    public List<AppDefinitionRepresentation> getAppDefinitions() {
        return this.appDefinitions;
    }

    public void setAppDefinitions(List<AppDefinitionRepresentation> appDefinitions) {
        this.appDefinitions = appDefinitions;
    }
}
