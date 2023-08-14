package com.innov.workflow.activiti.service.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.custom.dao.ActFormDefinitionService;
import com.innov.workflow.activiti.custom.form.ActFormDefinition;
import com.innov.workflow.activiti.domain.editor.AppDefinition;
import com.innov.workflow.activiti.domain.editor.AppModelDefinition;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.repository.editor.ModelRepository;
import com.innov.workflow.activiti.service.api.AppDefinitionService;
import com.innov.workflow.activiti.service.api.DeploymentService;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.dmn.model.DmnDefinition;
import org.activiti.dmn.xml.converter.DmnXMLConverter;
import org.activiti.editor.dmn.converter.DmnJsonConverter;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.persistence.entity.DeploymentEntityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.form.api.FormDeployment;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.engine.FormEngine;
import org.activiti.form.model.FormDefinition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.*;

@Service
public class DeploymentServiceImpl implements DeploymentService {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentServiceImpl.class);
    @Autowired
    protected AppDefinitionService appDefinitionService;
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected ModelRepository modelRepository;
    @Autowired
    protected FormEngine formEngine;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected FormRepositoryService formRepositoryService;

    @Autowired
    protected ActFormDefinitionService actFormDefinitionService;


    protected DmnJsonConverter dmnJsonConverter = new DmnJsonConverter();
    protected DmnXMLConverter dmnXMLConverter = new DmnXMLConverter();

    public DeploymentServiceImpl() {
    }

    @Transactional
    public DeploymentEntityImpl updateAppDefinition(Model appDefinitionModel, User user) {
        DeploymentEntityImpl deployment = null;
        AppDefinition appDefinition = this.resolveAppDefinition(appDefinitionModel);


        if (CollectionUtils.isNotEmpty(appDefinition.getModels())) {
            DeploymentBuilder deploymentBuilder = this.repositoryService.createDeployment().name(appDefinitionModel.getName()).key(appDefinitionModel.getKey());
            Iterator i$ = appDefinition.getModels().iterator();
            Map<String, Model> formMap = new HashMap();
            Map<String, Model> decisionTableMap = new HashMap();
            Model decisionTableInfo;

            while (i$.hasNext()) {


                AppModelDefinition appModelDef = (AppModelDefinition) i$.next();
                decisionTableInfo = this.modelService.getModel(appModelDef.getId());
                if (decisionTableInfo == null) {
                    logger.error("Model " + appModelDef.getId() + " for app definition " + appDefinitionModel.getId() + " could not be found");
                    throw new BadRequestException("Model for app definition could not be found");
                }

                List<Model> referencedModels = this.modelRepository.findModelsByParentModelId(decisionTableInfo.getId());
                Iterator j$ = referencedModels.iterator();

                while (j$.hasNext()) {
                    Model childModel = (Model) j$.next();
                    if (2 == childModel.getModelType()) {
                        formMap.put(childModel.getId(), childModel);
                    } else if (4 == childModel.getModelType()) {
                        decisionTableMap.put(childModel.getId(), childModel);
                    }
                }

                BpmnModel bpmnModel = this.modelService.getBpmnModel(decisionTableInfo, formMap, decisionTableMap);
                Map<String, StartEvent> startEventMap = this.processNoneStartEvents(bpmnModel);
                Iterator k$ = bpmnModel.getProcesses().iterator();

                while (k$.hasNext()) {
                    Process process = (Process) k$.next();
                    this.processUserTasks(process.getFlowElements(), process, startEventMap);
                }

                byte[] modelXML = this.modelService.getBpmnXML(bpmnModel);
                deploymentBuilder.addInputStream(decisionTableInfo.getKey().replaceAll(" ", "") + ".bpmn", new ByteArrayInputStream(modelXML));
            }

            String decisionTableId;
            if (formMap.size() > 0) {
                i$ = formMap.keySet().iterator();

                while (i$.hasNext()) {
                    decisionTableId = (String) i$.next();
                    decisionTableInfo = (Model) formMap.get(decisionTableId);
                    deploymentBuilder.addString("form-" + decisionTableInfo.getKey() + ".form", decisionTableInfo.getModelEditorJson());
                }
            }

            if (decisionTableMap.size() > 0) {
                i$ = decisionTableMap.keySet().iterator();

                while (i$.hasNext()) {
                    decisionTableId = (String) i$.next();
                    decisionTableInfo = (Model) decisionTableMap.get(decisionTableId);

                    try {
                        JsonNode decisionTableNode = this.objectMapper.readTree(decisionTableInfo.getModelEditorJson());
                        DmnDefinition dmnDefinition = this.dmnJsonConverter.convertToDmn(decisionTableNode, decisionTableInfo.getId(), decisionTableInfo.getVersion(), decisionTableInfo.getLastUpdated());
                        byte[] dmnXMLBytes = this.dmnXMLConverter.convertToXML(dmnDefinition);
                        deploymentBuilder.addBytes("dmn-" + decisionTableInfo.getKey() + ".dmn", dmnXMLBytes);
                    } catch (Exception var16) {
                        logger.error("Error converting decision table to XML " + decisionTableInfo.getName(), var16);
                        throw new InternalServerErrorException("Error converting decision table to XML " + decisionTableInfo.getName());
                    }
                }
            }

            deployment = (DeploymentEntityImpl) deploymentBuilder.deploy();
        }

        return deployment;
    }

    public void deployForm(Model appDefinitionModel) {
        AppDefinition appDefinition = this.resolveAppDefinition(appDefinitionModel);

        Iterator i = appDefinition.getModels().iterator();

            while (i.hasNext()) {
                AppModelDefinition appModelDef = (AppModelDefinition) i.next();
                Model processModel = this.modelService.getModel(appModelDef.getId());

                if (processModel == null) {
                    logger.error("Model " + appModelDef.getId() + " for app definition " + appDefinitionModel.getId() + " could not be found");
                    throw new BadRequestException("Model for app definition could not be found");
                }

                if(processModel.getModelType() == 0) {
                    createFormDeployment(processModel);
                }
            }
    }



    public void createFormDeployment (Model processModel) {
        List<Model> formModels = this.modelRepository.findModelsByParentModelId(processModel.getId());
        ProcessDefinition p = this.repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processModel.getKey()).orderByProcessDefinitionVersion().desc().list().get(0);

        if (p == null) {
            return;
        }

        for(Model f: formModels) {
//            FormDefinition formDefinition = null;
//            try {
//                formDefinition = formRepositoryService.getFormDefinitionByKey(f.getKey());
//            } catch (Exception e) {
//
//            }
//
//            if(formDefinition != null) continue;

            FormDefinition formDefinition = new FormDefinition();

            formDefinition.setKey(f.getKey());
            formDefinition.setName(f.getName());
            formDefinition.setDescription(f.getDescription());
            formDefinition.setVersion(f.getVersion());

            String resourceName = "form-" + f.getKey() + ".form";

            FormDeployment formDeployment = formRepositoryService.createDeployment()
                    .name(f.getName())
                    .parentDeploymentId(p.getDeploymentId())
                    .addFormDefinition(resourceName, formDefinition)
                    .deploy();

            ActFormDefinition actFormDefinition = new ActFormDefinition();

            actFormDefinition.setId(UUID.randomUUID().toString());
            actFormDefinition.setResourceName(resourceName);
            actFormDefinition.setVersion(f.getVersion());
            actFormDefinition.setKey(f.getKey());
            actFormDefinition.setName(f.getName());
            actFormDefinition.setTenantId(formDeployment.getTenantId());
            actFormDefinition.setParentDeploymentId(formDeployment.getParentDeploymentId());
            actFormDefinition.setDeploymentId(formDeployment.getId());
            actFormDefinition.setDescription(f.getDescription());

            actFormDefinitionService.save(actFormDefinition);
        }
    }


    @Transactional
    public void deleteAppDefinition(String appDefinitionId) {
        List<Deployment> deployments = this.repositoryService.createDeploymentQuery().deploymentKey(String.valueOf(appDefinitionId)).list();
        if (deployments != null) {
            Iterator i$ = deployments.iterator();

            while (i$.hasNext()) {
                Deployment deployment = (Deployment) i$.next();
                this.repositoryService.deleteDeployment(deployment.getId(), true);
            }
        }

    }

    protected Map<String, StartEvent> processNoneStartEvents(BpmnModel bpmnModel) {
        Map<String, StartEvent> startEventMap = new HashMap();
        Iterator i$ = bpmnModel.getProcesses().iterator();

        while (true) {
            while (i$.hasNext()) {
                Process process = (Process) i$.next();
                Iterator j$ = process.getFlowElements().iterator();

                while (j$.hasNext()) {
                    FlowElement flowElement = (FlowElement) j$.next();
                    if (flowElement instanceof StartEvent) {
                        StartEvent startEvent = (StartEvent) flowElement;
                        if (CollectionUtils.isEmpty(startEvent.getEventDefinitions())) {
                            if (StringUtils.isEmpty(startEvent.getInitiator())) {
                                startEvent.setInitiator("initiator");
                            }

                            startEventMap.put(process.getId(), startEvent);
                            break;
                        }
                    }
                }
            }

            return startEventMap;
        }
    }

    protected void processUserTasks(Collection<FlowElement> flowElements, Process process, Map<String, StartEvent> startEventMap) {
        Iterator i$ = flowElements.iterator();

        while (i$.hasNext()) {
            FlowElement flowElement = (FlowElement) i$.next();
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                if ("$INITIATOR".equals(userTask.getAssignee()) && startEventMap.get(process.getId()) != null) {
                    userTask.setAssignee("${" + ((StartEvent) startEventMap.get(process.getId())).getInitiator() + "}");
                }
            } else if (flowElement instanceof SubProcess) {
                this.processUserTasks(((SubProcess) flowElement).getFlowElements(), process, startEventMap);
            }
        }

    }

    protected AppDefinition resolveAppDefinition(Model appDefinitionModel) {
        try {
            AppDefinition appDefinition = (AppDefinition) this.objectMapper.readValue(appDefinitionModel.getModelEditorJson(), AppDefinition.class);
            return appDefinition;
        } catch (Exception var3) {
            logger.error("Error deserializing app " + appDefinitionModel.getId(), var3);
            throw new InternalServerErrorException("Could not deserialize app definition");
        }
    }
}
