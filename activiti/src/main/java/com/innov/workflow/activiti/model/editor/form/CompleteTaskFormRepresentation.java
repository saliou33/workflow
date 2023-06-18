package com.innov.workflow.activiti.model.editor.form;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class CompleteTaskFormRepresentation {
    private Map<String, Object> values = new HashMap();
    private String outcome;
}
