package com.innov.workflow.app.dto.activiti;

import com.innov.workflow.core.domain.entity.Organization;
import com.innov.workflow.core.domain.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class ApplicationDto {

    private Long id;
    private String name;
    private User owner;
    private String scope;
    private String state;
    private String logo;
    private String appKey;
    private String description;
    private Integer version;
    private List<ProcessDefinitionDto> processDefinitions;
    private Organization organization;
}
