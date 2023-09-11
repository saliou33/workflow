package com.innov.workflow.activiti.service.editor;

import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.runtime.DeploymentServiceImpl;
import com.innov.workflow.core.domain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppDefinitionPublishService {
    private static final Logger logger = LoggerFactory.getLogger(AppDefinitionPublishService.class);
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected DeploymentServiceImpl deploymentService;

    public AppDefinitionPublishService() {
    }

    public void publishAppDefinition(String comment, Model appDefinitionModel, User user) {
        this.modelService.createNewModelVersion(appDefinitionModel, comment, user);
        this.deploymentService.updateAppDefinition(appDefinitionModel, user);
        this.deploymentService.deployForm(appDefinitionModel);
    }
}
