package com.innov.workflow.core.domain.activiti;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class FlowInfo {
    String processInstanceId;

    String businessKey;

    String name;

    Boolean suspended;

    Boolean ended;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date endTime;

    String startUserId;

    String currentTask;

    String assignee;

}
