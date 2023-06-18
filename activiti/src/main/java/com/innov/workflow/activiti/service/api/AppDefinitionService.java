package com.innov.workflow.activiti.service.api;

import com.innov.workflow.core.domain.entity.User;

import java.util.List;

public interface AppDefinitionService {
    List<AppDefinitionServiceRepresentation> getAppDefinitions();

    List<AppDefinitionServiceRepresentation> getDeployableAppDefinitions(User user);

    String getDefinitionIdForModelAndUser(String id, User user);
}
