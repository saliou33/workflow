package com.innov.workflow.activiti.service.api;

import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.engine.repository.Deployment;
import org.springframework.transaction.annotation.Transactional;

public interface DeploymentService {
    @Transactional
    Deployment updateAppDefinition(Model var1, User var2);

    @Transactional
    void deleteAppDefinition(String var1);
}