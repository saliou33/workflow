package com.innov.workflow.app.controller.activiti;

import com.innov.workflow.app.dto.PaginationDTO;
import com.innov.workflow.app.service.ActivitiService;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.activiti.ProcessInstanceInfo;
import lombok.AllArgsConstructor;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/activiti/process-instance")
@AllArgsConstructor
public class ProcessInstanceController {

    private final RuntimeService runtimeService;

    private final ActivitiService activitiService;

    @PostMapping("/create/{processDefinitionId}")
    public ResponseEntity createProcessInstance(@PathVariable String processDefinitionId, @RequestBody HashMap<String, Object> variables) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);
        return ApiResponse.success("process instance created", processInstance);
    }


    @GetMapping("/all")
    public ResponseEntity getAllProcessInstances(PaginationDTO page) {
        List<String> processInstanceIds = new ArrayList<>();

        // Retrieve a list of all process instances
        List<ProcessInstance> processInstanceList =  runtimeService
                .createProcessInstanceQuery()
                .listPage(page.getStart(), page.getPageSize());

        List<ProcessInstanceInfo> infos = new ArrayList<>();

        for (ProcessInstance processInstance : processInstanceList) {
            infos.add(activitiService.processInstanceMap(processInstance));
        }


        return ApiResponse.success(infos);
    }

    @GetMapping("/by/{processInstanceId}")
    public ResponseEntity getProcessInstancesByDefinitionId(@PathVariable String processInstanceId, PaginationDTO page) {
        List<String> processInstanceIds = new ArrayList<>();


        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery()
                .processDefinitionId(processInstanceId)
                .listPage(page.getStart(), page.getPageSize());

        // Process the process instances and extract the IDs
        for (ProcessInstance processInstance : processInstanceList) {
            processInstanceIds.add(processInstance.getId());
        }

        return ApiResponse.success(processInstanceList);
    }

}
