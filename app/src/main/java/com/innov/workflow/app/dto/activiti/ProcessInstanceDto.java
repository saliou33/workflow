package com.innov.workflow.app.dto.activiti;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ProcessInstanceDto {

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

