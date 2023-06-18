package com.innov.workflow.activiti.model.runtime;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CreateTaskRepresentation extends AbstractRepresentation {
    protected String name;
    protected String description;
    protected String category;
}
