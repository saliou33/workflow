package com.innov.workflow.activiti.model.common;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseRestActionRepresentation {
    protected String action;
    protected String comment;

}
