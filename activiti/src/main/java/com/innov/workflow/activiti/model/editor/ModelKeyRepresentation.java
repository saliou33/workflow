package com.innov.workflow.activiti.model.editor;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModelKeyRepresentation extends AbstractRepresentation {
    protected boolean keyAlreadyExists;
    protected String key;
    protected String id;
    protected String name;

}
