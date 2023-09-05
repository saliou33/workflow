package com.innov.workflow.activiti.rest.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.runtime.ProcessInstanceRepresentation;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public abstract class AbstractProcessInstanceQueryResource {
    private static final int DEFAULT_PAGE_SIZE = 25;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected IdentityService identityService;

    public AbstractProcessInstanceQueryResource() {
    }

    public ResultListDataRepresentation getProcessInstances(ObjectNode requestNode) {
        HistoricProcessInstanceQuery instanceQuery = this.historyService.createHistoricProcessInstanceQuery();
        User currentUser = identityService.getCurrentUserObject();
        instanceQuery.involvedUser(String.valueOf(currentUser.getId()));
        JsonNode processDefinitionIdNode = requestNode.get("processDefinitionId");
        if (processDefinitionIdNode != null && !processDefinitionIdNode.isNull()) {
            instanceQuery.processDefinitionId(processDefinitionIdNode.asText());
        }

        JsonNode processDefinitionKeyNode = requestNode.get("processDefinitionKey");
        if (processDefinitionKeyNode != null && !processDefinitionKeyNode.isNull()) {
            instanceQuery.processDefinitionKey(processDefinitionKeyNode.asText());
        }

        JsonNode deploymentKeyNode = requestNode.get("deploymentKey");
        if (deploymentKeyNode != null && !deploymentKeyNode.isNull()) {
            List<Deployment> deployments = this.repositoryService.createDeploymentQuery().deploymentKey(deploymentKeyNode.asText()).list();
            List<String> deploymentIds = new ArrayList();
            Iterator i$ = deployments.iterator();

            while (i$.hasNext()) {
                Deployment deployment = (Deployment) i$.next();
                deploymentIds.add(deployment.getId());
            }

            instanceQuery.deploymentIdIn(deploymentIds);
        }

        JsonNode stateNode = requestNode.get("state");
        if (stateNode != null && !stateNode.isNull()) {
            String state = stateNode.asText();
            if ("running".equals(state)) {
                instanceQuery.unfinished();
            } else if ("completed".equals(state)) {
                instanceQuery.finished();
            } else if (!"all".equals(state)) {
                throw new BadRequestException("Illegal state filter value passed, only 'running', 'completed' or 'all' are supported");
            }
        } else {
            instanceQuery.unfinished();
        }

        JsonNode sortNode = requestNode.get("sort");
        if (sortNode != null && !sortNode.isNull()) {
            if ("created-desc".equals(sortNode.asText())) {
                instanceQuery.orderByProcessInstanceStartTime().desc();
            } else if ("created-asc".equals(sortNode.asText())) {
                instanceQuery.orderByProcessInstanceStartTime().asc();
            } else if ("ended-desc".equals(sortNode.asText())) {
                instanceQuery.orderByProcessInstanceEndTime().desc();
            } else if ("ended-asc".equals(sortNode.asText())) {
                instanceQuery.orderByProcessInstanceEndTime().asc();
            }
        } else {
            instanceQuery.orderByProcessInstanceStartTime().desc();
        }

        int page = 0;
        JsonNode pageNode = requestNode.get("page");
        if (pageNode != null && !pageNode.isNull()) {
            page = pageNode.asInt(0);
        }

        int size = 25;
        JsonNode sizeNode = requestNode.get("size");
        if (sizeNode != null && !sizeNode.isNull()) {
            size = sizeNode.asInt(25);
        }

        List<HistoricProcessInstance> instances = instanceQuery.listPage(page * size, size);
        ResultListDataRepresentation result = new ResultListDataRepresentation(this.convertInstanceList(instances));
        if (page != 0 || instances.size() == size) {
            Long totalCount = instanceQuery.count();
            result.setTotal((long) totalCount.intValue());
            result.setStart(page * size);
        }

        return result;
    }

    protected List<ProcessInstanceRepresentation> convertInstanceList(List<HistoricProcessInstance> instances) {
        List<ProcessInstanceRepresentation> result = new ArrayList();
        if (CollectionUtils.isNotEmpty(instances)) {
            Iterator i$ = instances.iterator();

            while (i$.hasNext()) {
                HistoricProcessInstance processInstance = (HistoricProcessInstance) i$.next();
                User userRep = null;
                if (processInstance.getStartUserId() != null) {
                    userRep = this.identityService.getUser(processInstance.getStartUserId());
                }

                ProcessDefinitionEntity procDef = (ProcessDefinitionEntity) this.repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
                ProcessInstanceRepresentation instanceRepresentation = new ProcessInstanceRepresentation(processInstance, procDef, procDef.isGraphicalNotationDefined(), userRep);
                result.add(instanceRepresentation);
            }
        }

        return result;
    }
}
