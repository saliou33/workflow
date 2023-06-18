package com.innov.workflow.activiti.rest.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.runtime.AppDefinitionRepresentation;
import com.innov.workflow.activiti.service.exception.NotFoundException;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import org.activiti.engine.repository.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class AppDefinitionsResource extends AbstractAppDefinitionsResource {
    private final Logger logger = LoggerFactory.getLogger(AppDefinitionsResource.class);
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected PermissionService permissionService;

    public AppDefinitionsResource() {
    }

    @RequestMapping(
            value = {"/rest/runtime/app-definitions"},
            method = {RequestMethod.GET}
    )
    public ResultListDataRepresentation getAppDefinitions() {
        return super.getAppDefinitions();
    }

    @RequestMapping(
            value = {"/rest/runtime/app-definitions/{deploymentKey}"},
            method = {RequestMethod.GET}
    )
    public AppDefinitionRepresentation getAppDefinition(@PathVariable("deploymentKey") String deploymentKey) {
        Deployment deployment = (Deployment) this.repositoryService.createDeploymentQuery().deploymentKey(deploymentKey).latest().singleResult();
        if (deployment == null) {
            throw new NotFoundException("No app definition is found with key: " + deploymentKey);
        } else {
            return this.createRepresentation(deployment);
        }
    }
}