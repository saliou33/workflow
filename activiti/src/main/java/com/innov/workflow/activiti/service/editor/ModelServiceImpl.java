package com.innov.workflow.activiti.service.editor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.domain.editor.*;
import com.innov.workflow.activiti.model.editor.ModelKeyRepresentation;
import com.innov.workflow.activiti.model.editor.ModelRepresentation;
import com.innov.workflow.activiti.model.editor.ReviveModelResultRepresentation;
import com.innov.workflow.activiti.repository.editor.ModelHistoryRepository;
import com.innov.workflow.activiti.repository.editor.ModelRelationRepository;
import com.innov.workflow.activiti.repository.editor.ModelRepository;
import com.innov.workflow.activiti.service.api.AppDefinitionService;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.activiti.service.exception.NotFoundException;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.editor.language.json.converter.util.JsonConverterUtil;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ModelServiceImpl implements ModelService {
    public static final String NAMESPACE = "http://activiti.com/modeler";
    protected static final String PROCESS_NOT_FOUND_MESSAGE_KEY = "PROCESS.ERROR.NOT-FOUND";
    private final Logger log = LoggerFactory.getLogger(ModelServiceImpl.class);
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected ModelImageService modelImageService;
    @Autowired
    protected ModelRepository modelRepository;
    @Autowired
    protected ModelHistoryRepository modelHistoryRepository;
    @Autowired
    protected ModelRelationRepository modelRelationRepository;
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected IdentityService identityService;

    @Autowired
    protected AppDefinitionService appDefinitionService;
    protected BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
    protected BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();

    public ModelServiceImpl() {
    }

    public Model getModel(String modelId) {
        Model model = (Model) this.modelRepository.findById(modelId).orElse(null);
        if (model == null) {
            NotFoundException modelNotFound = new NotFoundException("No model found with the given id: " + modelId);
            modelNotFound.setMessageKey("PROCESS.ERROR.NOT-FOUND");
            throw modelNotFound;
        } else {
            return model;
        }
    }

    public List<AbstractModel> getModelsByModelType(Integer modelType) {
        return new ArrayList(this.modelRepository.findModelsByModelType(modelType));
    }

    public ModelHistory getModelHistory(String modelId, String modelHistoryId) {
        Model model = this.getModel(modelId);
        ModelHistory modelHistory = (ModelHistory) this.modelHistoryRepository.findById(modelHistoryId).orElse(null);
        if (modelHistory != null && modelHistory.getRemovalDate() == null && modelHistory.getModelId().equals(model.getId())) {
            return modelHistory;
        } else {
            throw new NotFoundException("Process model history not found: " + modelHistoryId);
        }
    }

    public byte[] getBpmnXML(AbstractModel model) {
        BpmnModel bpmnModel = this.getBpmnModel(model);
        return this.getBpmnXML(bpmnModel);
    }

    public byte[] getBpmnXML(BpmnModel bpmnModel) {
        Iterator i$ = bpmnModel.getProcesses().iterator();

        while (i$.hasNext()) {
            Process process = (Process) i$.next();
            if (StringUtils.isNotEmpty(process.getId())) {
                char firstCharacter = process.getId().charAt(0);
                if (Character.isDigit(firstCharacter)) {
                    process.setId("a" + process.getId());
                }
            }
        }

        byte[] xmlBytes = this.bpmnXMLConverter.convertToXML(bpmnModel);
        return xmlBytes;
    }

    public ModelKeyRepresentation validateModelKey(Model model, Integer modelType, String key) {
        ModelKeyRepresentation modelKeyResponse = new ModelKeyRepresentation();
        modelKeyResponse.setKey(key);
        List<Model> models = this.modelRepository.findModelsByKeyAndType(key, modelType);
        Iterator i$ = models.iterator();

        while (i$.hasNext()) {
            Model modelInfo = (Model) i$.next();
            if (model == null || !modelInfo.getId().equals(model.getId())) {
                modelKeyResponse.setKeyAlreadyExists(true);
                modelKeyResponse.setId(modelInfo.getId());
                modelKeyResponse.setName(modelInfo.getName());
                break;
            }
        }

        return modelKeyResponse;
    }

    @Transactional
    public Model createModel(Model newModel, User createdBy) {
        newModel.setVersion(1);
        newModel.setCreated(Calendar.getInstance().getTime());
        newModel.setCreatedBy(createdBy.getId());
        newModel.setLastUpdated(Calendar.getInstance().getTime());
        newModel.setLastUpdatedBy(createdBy.getId());
        this.persistModel(newModel);
        return newModel;
    }

    @Transactional
    public Model createModel(ModelRepresentation model, String editorJson, User createdBy) {
        Model newModel = new Model();
        newModel.setVersion(1);
        newModel.setName(model.getName());
        newModel.setKey(model.getKey());
        newModel.setModelType(model.getModelType());
        newModel.setCreated(Calendar.getInstance().getTime());
        newModel.setCreatedBy(createdBy.getId());
        newModel.setDescription(model.getDescription());
        newModel.setModelEditorJson(editorJson);
        newModel.setLastUpdated(Calendar.getInstance().getTime());
        newModel.setLastUpdatedBy(createdBy.getId());
        this.persistModel(newModel);
        return newModel;
    }

    @Transactional
    public Model createNewModelVersion(Model modelObject, String comment, User updatedBy) {
        return (Model) this.internalCreateNewModelVersion(modelObject, comment, updatedBy, false);
    }

    @Transactional
    public ModelHistory createNewModelVersionAndReturnModelHistory(Model modelObject, String comment, User updatedBy) {
        return (ModelHistory) this.internalCreateNewModelVersion(modelObject, comment, updatedBy, true);
    }

    protected AbstractModel internalCreateNewModelVersion(Model modelObject, String comment, User updatedBy, boolean returnModelHistory) {
        modelObject.setLastUpdated(new Date());
        modelObject.setLastUpdatedBy(updatedBy.getId());
        modelObject.setComment(comment);
        ModelHistory historyModel = this.createNewModelhistory(modelObject);
        this.persistModelHistory(historyModel);
        modelObject.setVersion(modelObject.getVersion() + 1);
        this.persistModel(modelObject);
        return (AbstractModel) (returnModelHistory ? historyModel : modelObject);
    }

    public Model saveModel(Model modelObject) {
        return this.persistModel(modelObject);
    }

    @Transactional
    public Model saveModel(Model modelObject, String editorJson, byte[] imageBytes, boolean newVersion, String newVersionComment, User updatedBy) {
        return this.internalSave(modelObject.getName(), modelObject.getKey(), modelObject.getDescription(), editorJson, newVersion, newVersionComment, imageBytes, updatedBy, modelObject);
    }

    @Transactional
    public Model saveModel(String modelId, String name, String key, String description, String editorJson, boolean newVersion, String newVersionComment, User updatedBy) {
        Model modelObject = (Model) this.modelRepository.findById(modelId).orElse(null);
        return this.internalSave(name, key, description, editorJson, newVersion, newVersionComment, (byte[]) null, updatedBy, modelObject);
    }

    protected Model internalSave(String name, String key, String description, String editorJson, boolean newVersion, String newVersionComment, byte[] imageBytes, User updatedBy, Model modelObject) {
        if (!newVersion) {
            modelObject.setLastUpdated(new Date());
            modelObject.setLastUpdatedBy(updatedBy.getId());
            modelObject.setName(name);
            modelObject.setKey(key);
            modelObject.setDescription(description);
            modelObject.setModelEditorJson(editorJson);
            if (imageBytes != null) {
                modelObject.setThumbnail(imageBytes);
            }
        } else {
            ModelHistory historyModel = this.createNewModelhistory(modelObject);
            this.persistModelHistory(historyModel);
            modelObject.setVersion(modelObject.getVersion() + 1);
            modelObject.setLastUpdated(new Date());
            modelObject.setLastUpdatedBy(updatedBy.getId());
            modelObject.setName(name);
            modelObject.setKey(key);
            modelObject.setDescription(description);
            modelObject.setModelEditorJson(editorJson);
            modelObject.setComment(newVersionComment);
            if (imageBytes != null) {
                modelObject.setThumbnail(imageBytes);
            }
        }

        return this.persistModel(modelObject);
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

    @Transactional
    public void deleteModel(String modelId, boolean cascadeHistory, boolean deleteRuntimeApp) {
        Model model = (Model) this.modelRepository.findById(modelId).orElse(null);
        if (model == null) {
            throw new IllegalArgumentException("No model found with id: " + modelId);
        } else {
            List<ModelHistory> history = this.modelHistoryRepository.findByModelIdAndRemovalDateIsNullOrderByVersionDesc(model.getId());
            ModelHistory toRevive;
            if (deleteRuntimeApp && model.getModelType() == 3) {
                String appDefinitionId = this.appDefinitionService.getDefinitionIdForModelAndUser(model.getId(), identityService.getCurrentUserObject());
                if (appDefinitionId != null) {
                    deleteAppDefinition(appDefinitionId);
                }
            } else {
                toRevive = this.createNewModelhistory(model);
                toRevive.setRemovalDate(Calendar.getInstance().getTime());
                this.persistModelHistory(toRevive);
            }

            if (!cascadeHistory && history.size() != 0) {
                toRevive = (ModelHistory) history.remove(0);
                this.populateModelBasedOnHistory(model, toRevive);
                this.persistModel(model);
                this.modelHistoryRepository.delete(toRevive);
            } else {
                this.deleteModelAndChildren(model);
            }

        }
    }

    protected void deleteModelAndChildren(Model model) {
        List<Model> allModels = new ArrayList();
        this.internalDeleteModelAndChildren(model, allModels);
        Iterator i$ = allModels.iterator();

        while (i$.hasNext()) {
            Model modelToDelete = (Model) i$.next();
            this.modelRepository.delete(modelToDelete);
        }

    }

    protected void internalDeleteModelAndChildren(Model model, List<Model> allModels) {
        this.modelRelationRepository.deleteModelRelationsForParentModel(model.getId());
        allModels.add(model);
    }

    @Transactional
    public ReviveModelResultRepresentation reviveProcessModelHistory(ModelHistory modelHistory, User user, String newVersionComment) {
        Model latestModel = (Model) this.modelRepository.findById(modelHistory.getModelId()).orElse(null);
        if (latestModel == null) {
            throw new IllegalArgumentException("No process model found with id: " + modelHistory.getModelId());
        } else {
            ModelHistory latestModelHistory = this.createNewModelhistory(latestModel);
            this.persistModelHistory(latestModelHistory);
            latestModel.setVersion(latestModel.getVersion() + 1);
            latestModel.setLastUpdated(new Date());
            latestModel.setLastUpdatedBy(user.getId());
            latestModel.setName(modelHistory.getName());
            latestModel.setKey(modelHistory.getKey());
            latestModel.setDescription(modelHistory.getDescription());
            latestModel.setModelEditorJson(modelHistory.getModelEditorJson());
            latestModel.setModelType(modelHistory.getModelType());
            latestModel.setComment(newVersionComment);
            this.persistModel(latestModel);
            ReviveModelResultRepresentation result = new ReviveModelResultRepresentation();
            if (latestModel.getModelType() == 3 && StringUtils.isNotEmpty(latestModel.getModelEditorJson())) {
                try {
                    AppDefinition appDefinition = (AppDefinition) this.objectMapper.readValue(latestModel.getModelEditorJson(), AppDefinition.class);
                    Iterator i$ = appDefinition.getModels().iterator();

                    while (i$.hasNext()) {
                        AppModelDefinition appModelDefinition = (AppModelDefinition) i$.next();
                        if (!this.modelRepository.existsById(appModelDefinition.getId())) {
                            result.getUnresolvedModels().add(new ReviveModelResultRepresentation.UnresolveModelRepresentation(appModelDefinition.getId(), appModelDefinition.getName(), appModelDefinition.getLastUpdatedBy()));
                            result.getUnresolvedModels().add(new ReviveModelResultRepresentation.UnresolveModelRepresentation(appModelDefinition.getId(), appModelDefinition.getName(), appModelDefinition.getLastUpdatedBy()));
                        }
                    }
                } catch (Exception var10) {
                    this.log.error("Could not deserialize app model json (id = " + latestModel.getId() + ")", var10);
                }
            }

            return result;
        }
    }

    public BpmnModel getBpmnModel(AbstractModel model) {
        BpmnModel bpmnModel = null;

        try {
            Map<String, Model> formMap = new HashMap();
            Map<String, Model> decisionTableMap = new HashMap();
            List<Model> referencedModels = this.modelRepository.findModelsByParentModelId(model.getId());
            Iterator i$ = referencedModels.iterator();

            while (i$.hasNext()) {
                Model childModel = (Model) i$.next();
                if (2 == childModel.getModelType()) {
                    formMap.put(childModel.getId(), childModel);
                } else if (4 == childModel.getModelType()) {
                    decisionTableMap.put(childModel.getId(), childModel);
                }
            }

            bpmnModel = this.getBpmnModel(model, formMap, decisionTableMap);
            return bpmnModel;
        } catch (Exception var8) {
            this.log.error("Could not generate BPMN 2.0 model for " + model.getId(), var8);
            throw new InternalServerErrorException("Could not generate BPMN 2.0 model");
        }
    }

    public BpmnModel getBpmnModel(AbstractModel model, Map<String, Model> formMap, Map<String, Model> decisionTableMap) {
        try {
            ObjectNode editorJsonNode = (ObjectNode) this.objectMapper.readTree(model.getModelEditorJson());
            Map<String, String> formKeyMap = new HashMap();
            Iterator i$ = formMap.values().iterator();

            while (i$.hasNext()) {
                Model formModel = (Model) i$.next();
                formKeyMap.put(formModel.getId(), formModel.getKey());
            }

            Map<String, String> decisionTableKeyMap = new HashMap();
            Iterator j$ = decisionTableMap.values().iterator();

            while (j$.hasNext()) {
                Model decisionTableModel = (Model) j$.next();
                decisionTableKeyMap.put(decisionTableModel.getId(), decisionTableModel.getKey());
            }

            return this.bpmnJsonConverter.convertToBpmnModel(editorJsonNode, formKeyMap, decisionTableKeyMap);
        } catch (Exception var9) {
            this.log.error("Could not generate BPMN 2.0 model for " + model.getId(), var9);
            throw new InternalServerErrorException("Could not generate BPMN 2.0 model");
        }
    }

    protected void addOrUpdateExtensionElement(String name, String value, UserTask userTask) {
        List<ExtensionElement> extensionElements = (List) userTask.getExtensionElements().get(name);
        ExtensionElement extensionElement;
        if (CollectionUtils.isNotEmpty(extensionElements)) {
            extensionElement = (ExtensionElement) extensionElements.get(0);
        } else {
            extensionElement = new ExtensionElement();
        }

        extensionElement.setNamespace("http://activiti.com/modeler");
        extensionElement.setNamespacePrefix("modeler");
        extensionElement.setName(name);
        extensionElement.setElementText(value);
        if (CollectionUtils.isEmpty(extensionElements)) {
            userTask.addExtensionElement(extensionElement);
        }

    }

    public Long getModelCountForUser(User user, int modelType) {
        return this.modelRepository.countByModelTypeAndUser(modelType, user.getId());
    }

    protected Model persistModel(Model model) {
        model = (Model) this.modelRepository.save(model);
        if (StringUtils.isNotEmpty(model.getModelEditorJson())) {
            ObjectNode jsonNode = null;

            try {
                jsonNode = (ObjectNode) this.objectMapper.readTree(model.getModelEditorJson());
            } catch (Exception var4) {
                this.log.error("Could not deserialize json model", var4);
                throw new InternalServerErrorException("Could not deserialize json model");
            }

            if (model.getModelType() != null && model.getModelType() != 0) {
                if (model.getModelType() != 2 && model.getModelType() != 4) {
                    if (model.getModelType() == 3) {
                        this.handleAppModelProcessRelations(model, jsonNode);
                    }
                } else {
                    jsonNode.put("name", model.getName());
                    jsonNode.put("key", model.getKey());
                }
            } else {
                this.modelImageService.generateThumbnailImage(model, jsonNode);
                this.handleBpmnProcessFormModelRelations(model, jsonNode);
                this.handleBpmnProcessDecisionTaskModelRelations(model, jsonNode);
            }
        }

        return model;
    }

    protected ModelHistory persistModelHistory(ModelHistory modelHistory) {
        return (ModelHistory) this.modelHistoryRepository.save(modelHistory);
    }

    protected void handleBpmnProcessFormModelRelations(AbstractModel bpmnProcessModel, ObjectNode editorJsonNode) {
        List<JsonNode> formReferenceNodes = JsonConverterUtil.filterOutJsonNodes(JsonConverterUtil.getBpmnProcessModelFormReferences(editorJsonNode));
        Set<String> formIds = JsonConverterUtil.gatherStringPropertyFromJsonNodes(formReferenceNodes, "id");
        this.handleModelRelations(bpmnProcessModel, formIds, "form-model");
    }

    protected void handleBpmnProcessDecisionTaskModelRelations(AbstractModel bpmnProcessModel, ObjectNode editorJsonNode) {
        List<JsonNode> decisionTableNodes = JsonConverterUtil.filterOutJsonNodes(JsonConverterUtil.getBpmnProcessModelDecisionTableReferences(editorJsonNode));
        Set<String> decisionTableIds = JsonConverterUtil.gatherStringPropertyFromJsonNodes(decisionTableNodes, "id");
        this.handleModelRelations(bpmnProcessModel, decisionTableIds, "decision-table-model");
    }

    protected void handleAppModelProcessRelations(AbstractModel appModel, ObjectNode appModelJsonNode) {
        Set<String> processModelIds = JsonConverterUtil.getAppModelReferencedModelIds(appModelJsonNode);
        this.handleModelRelations(appModel, processModelIds, "process-model");
    }

    protected void handleModelRelations(AbstractModel bpmnProcessModel, Set<String> idsReferencedInJson, String relationshipType) {
        List<ModelRelation> persistedModelRelations = this.modelRelationRepository.findByParentModelIdAndType(bpmnProcessModel.getId(), relationshipType);
        if (idsReferencedInJson != null && idsReferencedInJson.size() != 0) {
            Set<String> alreadyPersistedModelIds = new HashSet(persistedModelRelations.size());
            Iterator i$ = persistedModelRelations.iterator();

            while (i$.hasNext()) {
                ModelRelation persistedModelRelation = (ModelRelation) i$.next();
                if (!idsReferencedInJson.contains(persistedModelRelation.getModelId())) {
                    this.modelRelationRepository.delete(persistedModelRelation);
                } else {
                    alreadyPersistedModelIds.add(persistedModelRelation.getModelId());
                }
            }

            i$ = idsReferencedInJson.iterator();

            while (i$.hasNext()) {
                String idReferencedInJson = (String) i$.next();
                if (!alreadyPersistedModelIds.contains(idReferencedInJson) && this.modelRepository.existsById(idReferencedInJson)) {
                    this.modelRelationRepository.save(new ModelRelation(bpmnProcessModel.getId(), idReferencedInJson, relationshipType));
                }
            }

        } else {
            this.modelRelationRepository.deleteAll(persistedModelRelations);
        }
    }

    protected ModelHistory createNewModelhistory(Model model) {
        ModelHistory historyModel = new ModelHistory();
        historyModel.setName(model.getName());
        historyModel.setKey(model.getKey());
        historyModel.setDescription(model.getDescription());
        historyModel.setCreated(model.getCreated());
        historyModel.setLastUpdated(model.getLastUpdated());
        historyModel.setCreatedBy(model.getCreatedBy());
        historyModel.setLastUpdatedBy(model.getLastUpdatedBy());
        historyModel.setModelEditorJson(model.getModelEditorJson());
        historyModel.setModelType(model.getModelType());
        historyModel.setVersion(model.getVersion());
        historyModel.setModelId(model.getId());
        historyModel.setComment(model.getComment());
        return historyModel;
    }

    protected void populateModelBasedOnHistory(Model model, ModelHistory basedOn) {
        model.setName(basedOn.getName());
        model.setKey(basedOn.getKey());
        model.setDescription(basedOn.getDescription());
        model.setCreated(basedOn.getCreated());
        model.setLastUpdated(basedOn.getLastUpdated());
        model.setCreatedBy(basedOn.getCreatedBy());
        model.setLastUpdatedBy(basedOn.getLastUpdatedBy());
        model.setModelEditorJson(basedOn.getModelEditorJson());
        model.setModelType(basedOn.getModelType());
        model.setVersion(basedOn.getVersion());
        model.setComment(basedOn.getComment());
    }
}
