package com.innov.workflow.activiti.model.runtime;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateProcessInstanceRepresentation extends CompleteFormRepresentation {
    private String processDefinitionId;
    private String name;
}
