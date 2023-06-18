package com.innov.workflow.app.dto.activiti;

import lombok.Data;

@Data
public class ProcessDefinitionDto {
    private String id;
    private String deploymentId;
    private String name;
    private String resourceName;
    private String key;
    private Integer version;
    private Integer appVersion;
    private String diagramResourceName;
}
