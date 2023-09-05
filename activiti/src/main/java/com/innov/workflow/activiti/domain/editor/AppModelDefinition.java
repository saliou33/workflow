package com.innov.workflow.activiti.domain.editor;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class AppModelDefinition {
    protected String id;
    protected String name;
    protected Integer version;
    protected Integer modelType;
    protected String description;
    protected String key;

    protected Long stencilSetId;
    protected String createdBy;
    protected String lastUpdatedBy;
    protected Date lastUpdated;
}
