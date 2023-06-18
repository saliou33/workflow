package com.innov.workflow.activiti.service.editor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.domain.editor.AppDefinition;
import com.innov.workflow.activiti.domain.editor.AppModelDefinition;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.model.editor.AppDefinitionPublishRepresentation;
import com.innov.workflow.activiti.model.editor.AppDefinitionRepresentation;
import com.innov.workflow.activiti.model.editor.AppDefinitionUpdateResultRepresentation;
import com.innov.workflow.activiti.old.service.IdentityService;
import com.innov.workflow.activiti.repository.editor.ModelRepository;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.editor.language.json.model.ModelInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class AppDefinitionImportService {
    private static final Logger logger = LoggerFactory.getLogger(AppDefinitionImportService.class);
    @Autowired
    protected AppDefinitionPublishService appDefinitionPublishService;
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected ModelRepository modelRepository;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected IdentityService identityService;
    protected BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();

    public AppDefinitionImportService() {
    }

    public AppDefinitionRepresentation importAppDefinition(HttpServletRequest request, MultipartFile file) {
        try {
            InputStream is = file.getInputStream();
            String fileName = file.getOriginalFilename();
            return this.importAppDefinition(request, is, fileName, (Model) null, (Map) null, (Map) null, (Map) null);
        } catch (IOException var5) {
            throw new InternalServerErrorException("Error loading file", var5);
        }
    }

    public AppDefinitionRepresentation importAppDefinitionNewVersion(HttpServletRequest request, MultipartFile file, String appDefId) {
        try {
            InputStream is = file.getInputStream();
            String fileName = file.getOriginalFilename();
            Model appModel = this.modelService.getModel(appDefId);
            if (!appModel.getModelType().equals(3)) {
                throw new BadRequestException("No app definition found for id " + appDefId);
            } else {
                AppDefinitionRepresentation appDefinition = this.createAppDefinitionRepresentation(appModel);
                Map<String, Model> existingProcessModelMap = new HashMap();
                Map<String, Model> existingFormModelMap = new HashMap();
                Map<String, Model> existingDecisionTableMap = new HashMap();
                if (appDefinition.getDefinition() != null && CollectionUtils.isNotEmpty(appDefinition.getDefinition().getModels())) {
                    Iterator i$ = appDefinition.getDefinition().getModels().iterator();

                    while (i$.hasNext()) {
                        AppModelDefinition modelDef = (AppModelDefinition) i$.next();
                        Model processModel = this.modelService.getModel(modelDef.getId());
                        List<Model> referencedModels = this.modelRepository.findModelsByParentModelId(processModel.getId());
                        Iterator j$ = referencedModels.iterator();

                        while (j$.hasNext()) {
                            Model childModel = (Model) j$.next();
                            if (2 == childModel.getModelType()) {
                                existingFormModelMap.put(childModel.getKey(), childModel);
                            } else if (4 == childModel.getModelType()) {
                                existingDecisionTableMap.put(childModel.getKey(), childModel);
                            }
                        }

                        existingProcessModelMap.put(processModel.getKey(), processModel);
                    }
                }

                return this.importAppDefinition(request, is, fileName, appModel, existingProcessModelMap, existingFormModelMap, existingDecisionTableMap);
            }
        } catch (IOException var17) {
            throw new InternalServerErrorException("Error loading file", var17);
        }
    }

    protected AppDefinitionRepresentation importAppDefinition(HttpServletRequest request, InputStream is, String fileName, Model existingAppModel, Map<String, Model> existingProcessModelMap, Map<String, Model> existingFormModelMap, Map<String, Model> existingDecisionTableModelMap) {
        if (fileName != null && fileName.endsWith(".zip")) {
            Map<String, String> formMap = new HashMap();
            Map<String, String> decisionTableMap = new HashMap();
            Map<String, String> bpmnModelMap = new HashMap();
            Map<String, byte[]> thumbnailMap = new HashMap();
            Model appDefinitionModel = this.readZipFile(is, formMap, decisionTableMap, bpmnModelMap, thumbnailMap);
            if (StringUtils.isNotEmpty(appDefinitionModel.getKey()) && StringUtils.isNotEmpty(appDefinitionModel.getModelEditorJson())) {
                Map<String, Model> formKeyAndModelMap = this.importForms(formMap, thumbnailMap, existingFormModelMap);
                Map<String, Model> decisionTableKeyAndModelMap = this.importDecisionTables(decisionTableMap, thumbnailMap, existingDecisionTableModelMap);
                Map<String, Model> bpmnModelIdAndModelMap = this.importBpmnModels(bpmnModelMap, formKeyAndModelMap, decisionTableKeyAndModelMap, thumbnailMap, existingProcessModelMap);
                AppDefinitionRepresentation result = this.importAppDefinitionModel(appDefinitionModel, existingAppModel, bpmnModelIdAndModelMap);
                return result;
            } else {
                throw new BadRequestException("Could not find app definition json");
            }
        } else {
            throw new BadRequestException("Invalid file name, only .zip files are supported not " + fileName);
        }
    }

    public AppDefinitionUpdateResultRepresentation publishAppDefinition(String modelId, AppDefinitionPublishRepresentation publishModel) {
        User user = identityService.getCurrentUserObject();
        Model appModel = this.modelService.getModel(modelId);
        AppDefinitionRepresentation appDefinitionRepresentation = this.createAppDefinitionRepresentation(appModel);
        AppDefinitionUpdateResultRepresentation result = new AppDefinitionUpdateResultRepresentation();
        this.appDefinitionPublishService.publishAppDefinition(publishModel.getComment(), appModel, user);
        result.setAppDefinition(appDefinitionRepresentation);
        return result;
    }

    protected AppDefinitionRepresentation createAppDefinitionRepresentation(AbstractModel model) {
        AppDefinition appDefinition;

        try {
            appDefinition = (AppDefinition) this.objectMapper.readValue(model.getModelEditorJson(), AppDefinition.class);
        } catch (Exception var4) {
            logger.error("Error deserializing app " + model.getId(), var4);
            throw new InternalServerErrorException("Could not deserialize app definition");
        }

        AppDefinitionRepresentation result = new AppDefinitionRepresentation(model);
        result.setDefinition(appDefinition);
        return result;
    }

    protected Model readZipFile(InputStream inputStream, Map<String, String> formMap, Map<String, String> decisionTableMap, Map<String, String> bpmnModelMap, Map<String, byte[]> thumbnailMap) {
        Model appDefinitionModel = null;
        ZipInputStream zipInputStream = null;

        try {
            zipInputStream = new ZipInputStream(inputStream);

            for (ZipEntry zipEntry = zipInputStream.getNextEntry(); zipEntry != null; zipEntry = zipInputStream.getNextEntry()) {
                String zipEntryName = zipEntry.getName();
                if (zipEntryName.endsWith("json") || zipEntryName.endsWith("png")) {
                    String modelFileName = null;
                    if (zipEntryName.contains("/")) {
                        modelFileName = zipEntryName.substring(zipEntryName.indexOf("/") + 1);
                    } else {
                        modelFileName = zipEntryName;
                    }

                    if (modelFileName.endsWith(".png")) {
                        thumbnailMap.put(modelFileName.replace(".png", ""), IOUtils.toByteArray(zipInputStream));
                    } else {
                        modelFileName = modelFileName.replace(".json", "");
                        String json = IOUtils.toString(zipInputStream);
                        if (zipEntryName.startsWith("bpmn-models/")) {
                            bpmnModelMap.put(modelFileName, json);
                        } else if (zipEntryName.startsWith("form-models/")) {
                            formMap.put(modelFileName, json);
                        } else if (zipEntryName.startsWith("decision-table-models/")) {
                            decisionTableMap.put(modelFileName, json);
                        } else if (!zipEntryName.contains("/")) {
                            appDefinitionModel = this.createModelObject(json, 3);
                        }
                    }
                }
            }
        } catch (Exception var22) {
            logger.error("Error reading app definition zip file", var22);
            throw new InternalServerErrorException("Error reading app definition zip file");
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.closeEntry();
                } catch (Exception var21) {
                }

                try {
                    zipInputStream.close();
                } catch (Exception var20) {
                }
            }

        }

        return appDefinitionModel;
    }

    protected Map<String, Model> importForms(Map<String, String> formMap, Map<String, byte[]> thumbnailMap, Map<String, Model> existingFormModelMap) {
        Map<String, Model> oldFormIdAndModelMap = new HashMap();

        String oldFormId;
        Model updatedFormModel;
        for (Iterator i$ = formMap.keySet().iterator(); i$.hasNext(); oldFormIdAndModelMap.put(oldFormId, updatedFormModel)) {
            String formKey = (String) i$.next();
            Model formModel = this.createModelObject((String) formMap.get(formKey), 2);
            oldFormId = formModel.getId();
            Model existingModel = null;
            if (existingFormModelMap != null && existingFormModelMap.containsKey(formModel.getKey())) {
                existingModel = (Model) existingFormModelMap.get(formModel.getKey());
            }

            updatedFormModel = null;
            if (existingModel != null) {
                byte[] imageBytes = null;
                if (thumbnailMap.containsKey(formKey)) {
                    imageBytes = (byte[]) thumbnailMap.get(formKey);
                }

                updatedFormModel = this.modelService.saveModel(existingModel, formModel.getModelEditorJson(), imageBytes, true, "App definition import", identityService.getCurrentUserObject());
            } else {
                formModel.setId((String) null);
                updatedFormModel = this.modelService.createModel(formModel, identityService.getCurrentUserObject());
                if (thumbnailMap.containsKey(formKey)) {
                    updatedFormModel.setThumbnail((byte[]) thumbnailMap.get(formKey));
                    this.modelRepository.save(updatedFormModel);
                }
            }
        }

        return oldFormIdAndModelMap;
    }

    protected Map<String, Model> importDecisionTables(Map<String, String> decisionTableMap, Map<String, byte[]> thumbnailMap, Map<String, Model> existingDecisionTableMap) {
        Map<String, Model> oldDecisionTableIdAndModelMap = new HashMap();

        String oldDecisionTableId;
        Model updatedDecisionTableModel;
        for (Iterator i$ = decisionTableMap.keySet().iterator(); i$.hasNext(); oldDecisionTableIdAndModelMap.put(oldDecisionTableId, updatedDecisionTableModel)) {
            String decisionTableKey = (String) i$.next();
            Model decisionTableModel = this.createModelObject((String) decisionTableMap.get(decisionTableKey), 4);
            oldDecisionTableId = decisionTableModel.getId();
            Model existingModel = null;
            if (existingDecisionTableMap != null && existingDecisionTableMap.containsKey(decisionTableModel.getKey())) {
                existingModel = (Model) existingDecisionTableMap.get(decisionTableModel.getKey());
            }

            updatedDecisionTableModel = null;
            if (existingModel != null) {
                byte[] imageBytes = null;
                if (thumbnailMap.containsKey(decisionTableKey)) {
                    imageBytes = (byte[]) thumbnailMap.get(decisionTableKey);
                }

                updatedDecisionTableModel = this.modelService.saveModel(existingModel, decisionTableModel.getModelEditorJson(), imageBytes, true, "App definition import", identityService.getCurrentUserObject());
            } else {
                decisionTableModel.setId((String) null);
                updatedDecisionTableModel = this.modelService.createModel(decisionTableModel, identityService.getCurrentUserObject());
                if (thumbnailMap.containsKey(decisionTableKey)) {
                    updatedDecisionTableModel.setThumbnail((byte[]) thumbnailMap.get(decisionTableKey));
                    this.modelRepository.save(updatedDecisionTableModel);
                }
            }
        }

        return oldDecisionTableIdAndModelMap;
    }

    protected Map<String, Model> importBpmnModels(Map<String, String> bpmnModelMap, Map<String, Model> formKeyAndModelMap, Map<String, Model> decisionTableKeyAndModelMap, Map<String, byte[]> thumbnailMap, Map<String, Model> existingProcessModelMap) {
        Map<String, Model> bpmnModelIdAndModelMap = new HashMap();

        String oldBpmnModelId;
        Model updatedProcessModel;
        for (Iterator i$ = bpmnModelMap.keySet().iterator(); i$.hasNext(); bpmnModelIdAndModelMap.put(oldBpmnModelId, updatedProcessModel)) {
            String bpmnModelKey = (String) i$.next();
            Model existingModel = null;
            if (existingProcessModelMap != null && existingProcessModelMap.containsKey(bpmnModelKey)) {
                existingModel = (Model) existingProcessModelMap.get(bpmnModelKey);
            }

            String bpmnModelJson = (String) bpmnModelMap.get(bpmnModelKey);
            Model bpmnModelObject = this.createModelObject(bpmnModelJson, 0);
            oldBpmnModelId = bpmnModelObject.getId();
            JsonNode bpmnModelNode = null;

            try {
                bpmnModelNode = this.objectMapper.readTree(bpmnModelObject.getModelEditorJson());
            } catch (Exception var22) {
                logger.error("Error reading BPMN json for " + bpmnModelKey, var22);
                throw new InternalServerErrorException("Error reading BPMN json for " + bpmnModelKey);
            }

            Map<String, String> oldFormIdFormKeyMap = new HashMap();
            Map<String, ModelInfo> formKeyModelIdMap = new HashMap();
            Iterator j$ = formKeyAndModelMap.keySet().iterator();

            while (j$.hasNext()) {
                String oldFormId = (String) j$.next();
                Model formModel = (Model) formKeyAndModelMap.get(oldFormId);
                oldFormIdFormKeyMap.put(oldFormId, formModel.getKey());
                formKeyModelIdMap.put(formModel.getKey(), new ModelInfo(formModel.getId(), formModel.getName(), formModel.getKey()));
            }

            Map<String, String> oldDecisionTableIdDecisionTableKeyMap = new HashMap();
            Map<String, ModelInfo> decisionTableKeyModelIdMap = new HashMap();
            Iterator k$ = decisionTableKeyAndModelMap.keySet().iterator();

            String updatedBpmnJson;
            while (k$.hasNext()) {
                updatedBpmnJson = (String) k$.next();
                updatedProcessModel = (Model) decisionTableKeyAndModelMap.get(updatedBpmnJson);
                oldDecisionTableIdDecisionTableKeyMap.put(updatedBpmnJson, updatedProcessModel.getKey());
                decisionTableKeyModelIdMap.put(updatedProcessModel.getKey(), new ModelInfo(updatedProcessModel.getId(), updatedProcessModel.getName(), updatedProcessModel.getKey()));
            }

            BpmnModel bpmnModel = this.bpmnJsonConverter.convertToBpmnModel(bpmnModelNode, oldFormIdFormKeyMap, oldDecisionTableIdDecisionTableKeyMap);
            updatedBpmnJson = this.bpmnJsonConverter.convertToJson(bpmnModel, formKeyModelIdMap, decisionTableKeyModelIdMap).toString();
            updatedProcessModel = null;
            if (existingModel != null) {
                byte[] imageBytes = null;
                if (thumbnailMap.containsKey(bpmnModelKey)) {
                    imageBytes = (byte[]) thumbnailMap.get(bpmnModelKey);
                }

                existingModel.setModelEditorJson(updatedBpmnJson);
                updatedProcessModel = this.modelService.saveModel(existingModel, existingModel.getModelEditorJson(), imageBytes, true, "App definition import", identityService.getCurrentUserObject());
            } else {
                bpmnModelObject.setId((String) null);
                bpmnModelObject.setModelEditorJson(updatedBpmnJson);
                updatedProcessModel = this.modelService.createModel(bpmnModelObject, identityService.getCurrentUserObject());
                if (thumbnailMap.containsKey(bpmnModelKey)) {
                    updatedProcessModel.setThumbnail((byte[]) thumbnailMap.get(bpmnModelKey));
                    this.modelService.saveModel(updatedProcessModel);
                }
            }
        }

        return bpmnModelIdAndModelMap;
    }

    protected AppDefinitionRepresentation importAppDefinitionModel(Model appDefinitionModel, Model existingAppModel, Map<String, Model> bpmnModelIdAndModelMap) {
        AppDefinition appDefinition = null;

        try {
            appDefinition = (AppDefinition) this.objectMapper.readValue(appDefinitionModel.getModelEditorJson(), AppDefinition.class);
        } catch (Exception var9) {
            logger.error("Error reading app definition " + appDefinitionModel.getName(), var9);
            throw new BadRequestException("Error reading app definition", var9);
        }

        Iterator i$ = appDefinition.getModels().iterator();

        while (i$.hasNext()) {
            AppModelDefinition modelDef = (AppModelDefinition) i$.next();
            if (bpmnModelIdAndModelMap.containsKey(modelDef.getId())) {
                Model newModel = (Model) bpmnModelIdAndModelMap.get(modelDef.getId());
                modelDef.setId(newModel.getId());
                modelDef.setName(newModel.getName());
                modelDef.setCreatedBy(newModel.getCreatedBy());
                modelDef.setLastUpdatedBy(newModel.getLastUpdatedBy());
                modelDef.setLastUpdated(newModel.getLastUpdated());
                modelDef.setVersion(newModel.getVersion());
            }
        }

        try {
            String updatedAppDefinitionJson = this.objectMapper.writeValueAsString(appDefinition);
            if (existingAppModel != null) {
                appDefinitionModel = this.modelService.saveModel(existingAppModel, updatedAppDefinitionJson, (byte[]) null, true, "App definition import", identityService.getCurrentUserObject());
            } else {
                appDefinitionModel.setId((String) null);
                appDefinitionModel.setModelEditorJson(updatedAppDefinitionJson);
                appDefinitionModel = this.modelService.createModel(appDefinitionModel, identityService.getCurrentUserObject());
            }

            AppDefinitionRepresentation result = new AppDefinitionRepresentation(appDefinitionModel);
            result.setDefinition(appDefinition);
            return result;
        } catch (Exception var8) {
            logger.error("Error storing app definition", var8);
            throw new InternalServerErrorException("Error storing app definition");
        }
    }

    protected Model createModelObject(String modelJson, int modelType) {
        try {
            JsonNode modelNode = this.objectMapper.readTree(modelJson);
            Model model = new Model();
            model.setId(modelNode.get("id").asText());
            model.setName(modelNode.get("name").asText());
            model.setKey(modelNode.get("key").asText());
            JsonNode descriptionNode = modelNode.get("description");
            if (descriptionNode != null && !descriptionNode.isNull()) {
                model.setDescription(descriptionNode.asText());
            }

            model.setModelEditorJson(modelNode.get("editorJson").toString());
            model.setModelType(modelType);
            return model;
        } catch (Exception var6) {
            logger.error("Error reading model json", var6);
            throw new InternalServerErrorException("Error reading model json");
        }
    }
}
