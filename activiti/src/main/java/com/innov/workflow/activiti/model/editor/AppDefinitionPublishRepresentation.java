package com.innov.workflow.activiti.model.editor;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppDefinitionPublishRepresentation extends AbstractRepresentation {
    protected String comment;
    protected Boolean force;
}
