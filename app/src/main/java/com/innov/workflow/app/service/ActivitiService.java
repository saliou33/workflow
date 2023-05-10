package com.innov.workflow.app.service;

import com.innov.workflow.core.domain.activiti.ProcessDefinitionInfo;
import com.innov.workflow.core.domain.activiti.ProcessInstanceInfo;
import com.innov.workflow.core.domain.activiti.TaskInfo;
import lombok.AllArgsConstructor;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Service
public class ActivitiService {

    private final TaskService taskService;

    private final RepositoryService repositoryService;

    public List<FormProperty> getFormProperties(String processDefinitionId, String taskId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        Collection<FlowElement> flowElementCollection = bpmnModel.getMainProcess().getFlowElements();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            System.out.println("task not found with id");
            return null;
        }
        String taskDefinitionKey = task.getTaskDefinitionKey();
        for (FlowElement flowElement: flowElementCollection) {
            System.out.println(flowElement.getName());
            if ("UserTask".equals(flowElement.getClass().getSimpleName()) && taskDefinitionKey.equals(flowElement.getId())) {
                return ((UserTask)flowElement).getFormProperties();
            }
        }
        return null;
    }


    public List<FormProperty> getFormProperties(String taskId) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            System.out.println("task not found with id");
            return null;
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());

        Collection<FlowElement> flowElementCollection = bpmnModel.getMainProcess().getFlowElements();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        for (FlowElement flowElement: flowElementCollection) {
            System.out.println(flowElement.getName());
            if ("UserTask".equals(flowElement.getClass().getSimpleName()) && taskDefinitionKey.equals(flowElement.getId())) {
                return ((UserTask)flowElement).getFormProperties();
            }
        }
        return null;
    }


    public List<FlowElement> getFlowElements(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        Collection<FlowElement> flowElementCollection = bpmnModel.getMainProcess().getFlowElements();
        return new ArrayList<>(flowElementCollection);
    }



    public ProcessInstanceInfo processInstanceMap(ProcessInstance p) {
        ProcessInstanceInfo info = new ProcessInstanceInfo();

        info.setId(p.getId());
        info.setProcessDefinitionId(p.getProcessDefinitionId());
        info.setProcessDefinitionName(p.getProcessDefinitionName());
        info.setProcessDefinitionKey(p.getProcessDefinitionKey());
        info.setName(p.getName());
        info.setBusinessKey(p.getBusinessKey());
        info.setStartTime(p.getStartTime());
        info.setSuspended(p.isSuspended());

        return info;
    }


    public TaskInfo taskMap (Task t) {
        TaskInfo info = new TaskInfo();

        info.setId(t.getId());
        info.setName(t.getName());
        info.setOwner(t.getOwner());
        info.setAssignee(t.getAssignee());
        info.setProcessInstanceId(t.getProcessInstanceId());
        info.setProcessDefinitionId(t.getProcessDefinitionId());
        info.setDescription(t.getDescription());
        info.setCategory(t.getCategory());
        info.setDueDate(t.getDueDate());
        info.setClaimTime(t.getClaimTime());
        info.setFormKey(t.getFormKey());
        info.setCreateTime(t.getCreateTime());

        return info;
    }

    public ProcessDefinitionInfo processDefinitionInfoMap(ProcessDefinition p)  {
        ProcessDefinitionInfo info = new ProcessDefinitionInfo();

        info.setId(p.getId());
        info.setKey(p.getKey());
        info.setVersion(p.getVersion());
        info.setDeploymentId(p.getDeploymentId());
        info.setDiagramResourceName(p.getDiagramResourceName());
        info.setAppVersion(p.getAppVersion());

        return info;
    }
}