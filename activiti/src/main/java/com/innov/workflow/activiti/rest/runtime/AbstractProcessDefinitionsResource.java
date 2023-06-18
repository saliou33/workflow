package com.innov.workflow.activiti.rest.runtime;

import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.runtime.ProcessDefinitionRepresentation;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.model.FormDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public abstract class AbstractProcessDefinitionsResource {
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected FormRepositoryService formRepositoryService;
    @Autowired
    protected PermissionService permissionService;

    public AbstractProcessDefinitionsResource() {
    }

    public ResultListDataRepresentation getProcessDefinitions(Boolean latest, String deploymentKey) {
        ProcessDefinitionQuery definitionQuery = this.repositoryService.createProcessDefinitionQuery();
        if (deploymentKey != null) {
            Deployment deployment = (Deployment) this.repositoryService.createDeploymentQuery().deploymentKey(deploymentKey).latest().singleResult();
            if (deployment == null) {
                return new ResultListDataRepresentation(new ArrayList());
            }

            definitionQuery.deploymentId(deployment.getId());
        } else if (latest != null && latest) {
            definitionQuery.latestVersion();
        }

        List<ProcessDefinition> definitions = definitionQuery.list();
        ResultListDataRepresentation result = new ResultListDataRepresentation(this.convertDefinitionList(definitions));
        return result;
    }

    protected List<ProcessDefinitionRepresentation> convertDefinitionList(List<ProcessDefinition> definitions) {
        Map<String, Boolean> startFormMap = new HashMap();
        List<ProcessDefinitionRepresentation> result = new ArrayList();
        if (CollectionUtils.isNotEmpty(definitions)) {
            Iterator i$ = definitions.iterator();

            while (i$.hasNext()) {
                ProcessDefinition processDefinition = (ProcessDefinition) i$.next();
                if (!startFormMap.containsKey(processDefinition.getId())) {
                    BpmnModel bpmnModel = this.repositoryService.getBpmnModel(processDefinition.getId());
                    List<StartEvent> startEvents = bpmnModel.getMainProcess().findFlowElementsOfType(StartEvent.class, false);
                    boolean hasStartForm = false;
                    Iterator j$ = startEvents.iterator();

                    while (j$.hasNext()) {
                        StartEvent startEvent = (StartEvent) j$.next();
                        if (StringUtils.isNotEmpty(startEvent.getFormKey())) {
                            FormDefinition formDefinition = this.formRepositoryService.getFormDefinitionByKey(startEvent.getFormKey());
                            if (formDefinition != null) {
                                hasStartForm = true;
                                break;
                            }
                        }
                    }

                    startFormMap.put(processDefinition.getId(), hasStartForm);
                }

                ProcessDefinitionRepresentation rep = new ProcessDefinitionRepresentation(processDefinition);
                rep.setHasStartForm((Boolean) startFormMap.get(processDefinition.getId()));
                result.add(rep);
            }
        }

        return result;
    }
}
