package com.innov.workflow.core.domain.activiti;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ProcessInstanceInfo {

    private String processDefinitionId;

    private String processDefinitionName;

    private String processDefinitionKey;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    private String id;

    private String name;

    private String deploymentId;

    private String businessKey;

    private Boolean suspended;

}

