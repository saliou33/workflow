package com.innov.workflow.activiti.service.api;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AppDefinitionServiceRepresentation extends AbstractRepresentation {
    protected String id;
    protected String name;
    protected String description;
    protected Integer version;
    protected String icon;
    protected String theme;
    protected List<String> models;
    protected String definition;
}
