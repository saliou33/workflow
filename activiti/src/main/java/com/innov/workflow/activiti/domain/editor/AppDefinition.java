package com.innov.workflow.activiti.domain.editor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@Data
public class AppDefinition {
    protected List<AppModelDefinition> models;
    protected String theme;
    protected String icon;
}
