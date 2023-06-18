package com.innov.workflow.activiti.model.editor.decisiontable;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DecisionTableDefinitionModelRepresentation {
    protected DecisionTableDefinitionRepresentation decisionTableDefinition;
    protected String description;
    protected String editorJson;
}
