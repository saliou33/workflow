package com.innov.workflow.activiti.model.runtime;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AppDefinitionRepresentation extends AbstractRepresentation {
    private String defaultAppId;
    private String name;
    private String description;
    private Long modelId;
    private String theme;
    private String icon;
    private String deploymentId;
    private String deploymentKey;
    private Long tenantId;

    public static AppDefinitionRepresentation createDefaultAppDefinitionRepresentation(String id) {
        AppDefinitionRepresentation appDefinitionRepresentation = new AppDefinitionRepresentation();
        appDefinitionRepresentation.setDefaultAppId(id);
        return appDefinitionRepresentation;
    }
}
