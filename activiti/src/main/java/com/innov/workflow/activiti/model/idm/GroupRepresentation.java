package com.innov.workflow.activiti.model.idm;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import com.innov.workflow.core.domain.entity.Group;
import lombok.Data;


@Data
public class GroupRepresentation extends AbstractRepresentation {
    protected String id;
    protected String name;
    protected String type;

    public GroupRepresentation() {
    }

    public GroupRepresentation(Group group) {
        this.setId(group.getId().toString());
        this.setName(group.getName());
        this.setType(group.getTag().getName());
    }
}
