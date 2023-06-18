package com.innov.workflow.activiti.model.editor;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FormValidationError {
    protected String validatorSetName;
    protected String problem;
    protected String defaultDescription;
    protected String fieldId;
    protected String fieldName;
    protected boolean isWarning;
}
