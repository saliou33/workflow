package com.innov.workflow.activiti.model.editor.decisiontable;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class DecisionTableExpressionRepresentation {
    public static final String VARIABLE_TYPE_VARIABLE = "variable";
    protected String id;
    protected String variableId;
    protected String variableType;
    protected String type;
    protected String label;
    protected List<Map<String, String>> entries;
    protected boolean newVariable;
}
