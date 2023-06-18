package com.innov.workflow.activiti.model.editor;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import com.innov.workflow.activiti.model.editor.decisiontable.DecisionTableRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class DecisionTableSaveRepresentation extends AbstractRepresentation {
    protected boolean reusable;
    protected boolean newVersion;
    protected String comment;
    protected String decisionTableImageBase64;
    protected DecisionTableRepresentation decisionTableRepresentation;

}
