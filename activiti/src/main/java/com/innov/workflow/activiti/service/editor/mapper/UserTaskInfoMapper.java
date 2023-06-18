package com.innov.workflow.activiti.service.editor.mapper;

import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserTaskInfoMapper extends AbstractInfoMapper {
    public UserTaskInfoMapper() {
    }

    protected void mapProperties(Object element) {
        UserTask userTask = (UserTask) element;
        this.createPropertyNode("Assignee", userTask.getAssignee());
        this.createPropertyNode("Candidate users", userTask.getCandidateUsers());
        this.createPropertyNode("Candidate groups", userTask.getCandidateGroups());
        this.createPropertyNode("Due date", userTask.getDueDate());
        this.createPropertyNode("Form key", userTask.getFormKey());
        this.createPropertyNode("Priority", userTask.getPriority());
        if (CollectionUtils.isNotEmpty(userTask.getFormProperties())) {
            List<String> formPropertyValues = new ArrayList();

            StringBuilder propertyBuilder;
            for (Iterator i$ = userTask.getFormProperties().iterator(); i$.hasNext(); formPropertyValues.add(propertyBuilder.toString())) {
                FormProperty formProperty = (FormProperty) i$.next();
                propertyBuilder = new StringBuilder();
                if (StringUtils.isNotEmpty(formProperty.getName())) {
                    propertyBuilder.append(formProperty.getName());
                } else {
                    propertyBuilder.append(formProperty.getId());
                }

                if (StringUtils.isNotEmpty(formProperty.getType())) {
                    propertyBuilder.append(" - ");
                    propertyBuilder.append(formProperty.getType());
                }

                if (formProperty.isRequired()) {
                    propertyBuilder.append(" (required)");
                } else {
                    propertyBuilder.append(" (not required)");
                }
            }

            this.createPropertyNode("Form properties", formPropertyValues);
        }

        this.createListenerPropertyNodes("Task listeners", userTask.getTaskListeners());
        this.createListenerPropertyNodes("Execution listeners", userTask.getExecutionListeners());
    }
}
