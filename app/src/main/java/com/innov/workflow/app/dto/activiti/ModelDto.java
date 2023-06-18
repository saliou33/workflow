package com.innov.workflow.app.dto.activiti;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.innov.workflow.core.domain.entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class ModelDto {
    private String id;
    private String name;
    private User owner;
    private String category;
    private String resourceId;
    private String deploymentId;
    private Integer version;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;
}
