package com.innov.workflow.activiti.model.runtime;

import com.innov.workflow.activiti.model.common.AbstractRepresentation;

import java.util.Date;

public class TaskUpdateRepresentation extends AbstractRepresentation {
    private String name;
    private String description;
    private Date dueDate;
    private boolean nameSet;
    private boolean descriptionSet;
    private boolean dueDateSet;

    public TaskUpdateRepresentation() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.nameSet = true;
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.descriptionSet = true;
        this.description = description;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDateSet = true;
        this.dueDate = dueDate;
    }

    public boolean isNameSet() {
        return this.nameSet;
    }

    public boolean isDescriptionSet() {
        return this.descriptionSet;
    }

    public boolean isDueDateSet() {
        return this.dueDateSet;
    }
}