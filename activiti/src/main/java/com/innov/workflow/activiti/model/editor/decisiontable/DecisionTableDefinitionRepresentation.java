package com.innov.workflow.activiti.model.editor.decisiontable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
@Data
@NoArgsConstructor
public class DecisionTableDefinitionRepresentation {
    protected String id;
    protected String name;
    protected String key;
    protected String description;
    protected String hitIndicator;
    protected String completenessIndicator;
    protected List<DecisionTableExpressionRepresentation> inputExpressions;
    protected List<DecisionTableExpressionRepresentation> outputExpressions;
    protected List<Map<String, Object>> rules;

}
