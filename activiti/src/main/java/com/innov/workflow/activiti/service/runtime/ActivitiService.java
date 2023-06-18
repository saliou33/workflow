package com.innov.workflow.activiti.service.runtime;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class ActivitiService {
    @Autowired
    private RuntimeService runtimeService;

    public ActivitiService() {
    }

    public ProcessInstance startProcessInstance(String processDefinitionId, Map<String, Object> variables, String processInstanceName) {
        ProcessInstance processInstance = this.runtimeService.startProcessInstanceById(processDefinitionId, variables);
        if (!processInstance.isEnded() && processInstanceName != null) {
            this.runtimeService.setProcessInstanceName(processInstance.getId(), processInstanceName);
        }

        return processInstance;
    }
}
