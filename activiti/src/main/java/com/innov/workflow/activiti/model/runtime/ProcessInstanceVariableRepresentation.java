package com.innov.workflow.activiti.model.runtime;


import lombok.Data;

@Data
public class ProcessInstanceVariableRepresentation {
    private String id;
    private String type;
    private Object value;

    public ProcessInstanceVariableRepresentation() {
    }

    public ProcessInstanceVariableRepresentation(String id, String type, Object value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }
}
