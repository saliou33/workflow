package com.innov.workflow.activiti.model.runtime;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
public class CompleteFormRepresentation {
    protected String formId;
    protected Map<String, Object> values;
    protected String outcome;
}
