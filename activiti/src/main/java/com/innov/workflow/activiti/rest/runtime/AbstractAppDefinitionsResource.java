package com.innov.workflow.activiti.rest.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.runtime.AppDefinitionRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public abstract class AbstractAppDefinitionsResource {
    protected static final AppDefinitionRepresentation kickstartAppDefinitionRepresentation = AppDefinitionRepresentation.createDefaultAppDefinitionRepresentation("kickstart");
    protected static final AppDefinitionRepresentation taskAppDefinitionRepresentation = AppDefinitionRepresentation.createDefaultAppDefinitionRepresentation("tasks");
    protected static final AppDefinitionRepresentation idmAppDefinitionRepresentation = AppDefinitionRepresentation.createDefaultAppDefinitionRepresentation("identity");
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected ObjectMapper objectMapper;

    public AbstractAppDefinitionsResource() {
    }

    protected ResultListDataRepresentation getAppDefinitions() {
        List<AppDefinitionRepresentation> resultList = new ArrayList();
        resultList.add(kickstartAppDefinitionRepresentation);
        resultList.add(taskAppDefinitionRepresentation);
        resultList.add(idmAppDefinitionRepresentation);
        Map<String, Deployment> deploymentMap = new HashMap();
        List<Deployment> deployments = this.repositoryService.createDeploymentQuery().list();
        Iterator i$ = deployments.iterator();

        Deployment deployment;
        while (i$.hasNext()) {
            deployment = (Deployment) i$.next();
            if (deployment.getKey() != null) {
                if (!deploymentMap.containsKey(deployment.getKey())) {
                    deploymentMap.put(deployment.getKey(), deployment);
                } else if (((Deployment) deploymentMap.get(deployment.getKey())).getDeploymentTime().before(deployment.getDeploymentTime())) {
                    deploymentMap.put(deployment.getKey(), deployment);
                }
            }
        }

        i$ = deploymentMap.values().iterator();

        while (i$.hasNext()) {
            deployment = (Deployment) i$.next();
            resultList.add(this.createRepresentation(deployment));
        }

        ResultListDataRepresentation result = new ResultListDataRepresentation(resultList);
        return result;
    }

    protected AppDefinitionRepresentation createDefaultAppDefinition(String id) {
        AppDefinitionRepresentation app = new AppDefinitionRepresentation();
        return app;
    }

    protected AppDefinitionRepresentation createRepresentation(Deployment deployment) {
        AppDefinitionRepresentation resultAppDef = new AppDefinitionRepresentation();
        resultAppDef.setDeploymentId(deployment.getId());
        resultAppDef.setDeploymentKey(deployment.getKey());
        resultAppDef.setName(deployment.getName());
        return resultAppDef;
    }
}
