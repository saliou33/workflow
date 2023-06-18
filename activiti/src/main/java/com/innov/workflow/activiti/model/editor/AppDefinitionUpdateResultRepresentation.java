package com.innov.workflow.activiti.model.editor;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AppDefinitionUpdateResultRepresentation extends AbstractRepresentation {
    public static final int CUSTOM_STENCIL_ITEM = 1;
    public static final int MODEL_VALIDATION_ERRORS = 2;
    protected AppDefinitionRepresentation appDefinition;
    protected String message;
    protected String messageKey;
    protected boolean error;
    protected int errorType;
    protected String errorDescription;
}
