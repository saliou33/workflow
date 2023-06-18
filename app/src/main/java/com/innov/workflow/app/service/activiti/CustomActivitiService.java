package com.innov.workflow.app.service.activiti;

import lombok.AllArgsConstructor;
import org.activiti.bpmn.model.*;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Service
public class CustomActivitiService {

    private final TaskService taskService;

    private final RepositoryService repositoryService;

    public List<FormProperty> getFormProperties(String taskId) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ActivitiException("task not found with id:" + taskId);
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Collection<FlowElement> flowElementCollection = bpmnModel.getMainProcess().getFlowElements();
        String taskDefinitionKey = task.getTaskDefinitionKey();

        for (FlowElement flowElement : flowElementCollection) {
            if (taskDefinitionKey.equals(flowElement.getId())) {
                if (flowElement.getClass().getSimpleName().equals("UserTask")) {
                    return ((UserTask) flowElement).getFormProperties();
                } else if (flowElement.getClass().getSimpleName().equals("StartEvent")) {
                    return ((StartEvent) flowElement).getFormProperties();
                }
            }
        }
        return null;
    }


    public List<FormProperty> getStartFormProperties(String processDefinitionId) {

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Collection<FlowElement> flowElementCollection = bpmnModel.getMainProcess().getFlowElements();
        for (FlowElement flowElement : flowElementCollection) {
            if ("StartEvent".equals(flowElement.getClass().getSimpleName())) {
                return ((StartEvent) flowElement).getFormProperties();
            }
        }
        return null;
    }

//    public List<FlowElement> getFlowElements(String processDefinitionId) {
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
//
//        Collection<FlowElement> flowElementCollection = bpmnModel.getMainProcess().getFlowElements();
//        return new ArrayList<>(flowElementCollection);
//    }
//


}