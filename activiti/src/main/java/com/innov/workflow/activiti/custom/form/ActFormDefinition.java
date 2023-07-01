package com.innov.workflow.activiti.custom.form;


import lombok.Data;
import javax.persistence.*;

@Table(name = "ACT_FO_FORM_DEFINITION")
@Data
public class ActFormDefinition {
    @Id
    @Column( name = "ID_")
    private String id;

    @Column(name = "NAME_")
    private String name;

    @Column(name = "VERSION_")
    private Integer version;

    @Column(name = "KEY_")
    private String key;

    @Column(name = "DEPLOYMENT_ID_")
    private String deploymentId;

    @Column(name = "TENANT_ID_")
    private String tenantId;

    @Column(name = "RESOURCE_NAME_")
    private String resourceName;

    @Column(name = "DESCRIPTION_")
    private String description;

    @Column(name = "PARENT_DEPLOYMENT_ID_")
    private String parentDeploymentId;

}
