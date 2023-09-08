package com.innov.workflow.activiti.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import com.innov.workflow.activiti.model.idm.UserRepresentation;
import lombok.Data;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;

import java.util.Date;
import java.util.List;


@Data
public class TaskRepresentation extends AbstractRepresentation {
    protected String id;
    protected String name;
    protected String description;
    protected String category;
    protected UserRepresentation assignee;
    protected Date created;
    protected Date dueDate;
    protected Date endDate;
    protected Long duration;
    protected Integer priority;
    protected String processInstanceId;
    protected String processInstanceName;
    protected String processDefinitionId;
    protected String processDefinitionName;
    protected String processDefinitionDescription;
    protected String processDefinitionKey;
    protected String processDefinitionCategory;
    protected int processDefinitionVersion;
    protected String processDefinitionDeploymentId;
    protected String formKey;
    protected String processInstanceStartUserId;
    protected boolean initiatorCanCompleteTask;
    protected boolean isMemberOfCandidateGroup;
    protected boolean isMemberOfCandidateUsers;
    @JsonDeserialize(
            contentAs = UserRepresentation.class
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected List<UserRepresentation> involvedPeople;

    public TaskRepresentation() {
    }

    public TaskRepresentation(Task task) {
        this(task, null);
    }

    public TaskRepresentation(HistoricTaskInstance task) {
        this(task, null);
    }

    public TaskRepresentation(TaskInfo taskInfo, ProcessDefinition processDefinition) {
        this.id = taskInfo.getId();
        this.name = taskInfo.getName();
        this.description = taskInfo.getDescription();
        this.category = taskInfo.getCategory();
        this.created = taskInfo.getCreateTime();
        this.dueDate = taskInfo.getDueDate();
        this.priority = taskInfo.getPriority();
        this.processInstanceId = taskInfo.getProcessInstanceId();
        this.processDefinitionId = taskInfo.getProcessDefinitionId();
        if (taskInfo instanceof HistoricTaskInstance) {
            this.endDate = ((HistoricTaskInstance) taskInfo).getEndTime();
            this.formKey = taskInfo.getFormKey();
            this.duration = ((HistoricTaskInstance) taskInfo).getDurationInMillis();
        } else {
            this.formKey = taskInfo.getFormKey();
        }

        if (processDefinition != null) {
            this.processDefinitionName = processDefinition.getName();
            this.processDefinitionDescription = processDefinition.getDescription();
            this.processDefinitionKey = processDefinition.getKey();
            this.processDefinitionCategory = processDefinition.getCategory();
            this.processDefinitionVersion = processDefinition.getVersion();
            this.processDefinitionDeploymentId = processDefinition.getDeploymentId();
        }

    }

    public TaskRepresentation(TaskInfo taskInfo, ProcessDefinition processDefinition, String processInstanceName) {
        this(taskInfo, processDefinition);
        this.processInstanceName = processInstanceName;
    }

    public void fillTask(Task task) {
        task.setName(this.name);
        task.setDescription(this.description);
        if (this.assignee != null && this.assignee.getId() != null) {
            task.setAssignee(this.assignee.getId());
        }

        task.setDueDate(this.dueDate);
        if (this.priority != null) {
            task.setPriority(this.priority);
        }

        task.setCategory(this.category);
    }

}
