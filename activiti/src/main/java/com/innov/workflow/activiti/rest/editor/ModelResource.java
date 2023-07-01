package com.innov.workflow.activiti.rest.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.model.editor.ModelKeyRepresentation;
import com.innov.workflow.activiti.model.editor.ModelRepresentation;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.repository.editor.ModelRepository;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.ConflictingRequestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class ModelResource extends AbstractModelResource {
    private static final Logger log = LoggerFactory.getLogger(ModelResource.class);
    private static final String RESOLVE_ACTION_OVERWRITE = "overwrite";
    private static final String RESOLVE_ACTION_SAVE_AS = "saveAs";
    private static final String RESOLVE_ACTION_NEW_VERSION = "newVersion";
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected ModelRepository modelRepository;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected IdentityService identityService;

    protected BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
    protected BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();

    public ModelResource() {
    }

    @RequestMapping(
            value = {"/activiti/models/{modelId}"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public ModelRepresentation getModel(@PathVariable String modelId) {
        return super.getModel(modelId);
    }

    @RequestMapping(
            value = {"/activiti/models/{modelId}/thumbnail"},
            method = {RequestMethod.GET},
            produces = {"image/png"}
    )
    public byte[] getModelThumbnail(@PathVariable String modelId) {
        return super.getModelThumbnail(modelId);
    }

    @RequestMapping(
            value = {"/activiti/models/{modelId}"},
            method = {RequestMethod.PUT}
    )
    public ModelRepresentation updateModel(@PathVariable String modelId, @RequestBody ModelRepresentation updatedModel) {
        Model model = this.modelService.getModel(modelId);
        ModelKeyRepresentation modelKeyInfo = this.modelService.validateModelKey(model, model.getModelType(), updatedModel.getKey());
        if (modelKeyInfo.isKeyAlreadyExists()) {
            throw new BadRequestException("Model with provided key already exists " + updatedModel.getKey());
        } else {
            try {
                updatedModel.updateModel(model);
                this.modelRepository.save(model);
                ModelRepresentation result = new ModelRepresentation(model);
                return result;
            } catch (Exception var6) {
                throw new BadRequestException("Model cannot be updated: " + modelId);
            }
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(
            value = {"/activiti/models/{modelId}"},
            method = {RequestMethod.DELETE}
    )
    public void deleteModel(@PathVariable String modelId, @RequestParam(required = false) Boolean cascade, @RequestParam(required = false) Boolean deleteRuntimeApp) {
        Model model = this.modelService.getModel(modelId);

        try {
            String currentUserId = identityService.getCurrentUserObject().getId().toString();
            boolean currentUserIsOwner = currentUserId.equals(model.getCreatedBy());
            if (currentUserIsOwner) {
                this.modelService.deleteModel(model.getId(), Boolean.TRUE.equals(cascade), Boolean.TRUE.equals(deleteRuntimeApp));
            }

        } catch (Exception var7) {
            log.error("Error while deleting: ", var7);
            throw new BadRequestException("Model cannot be deleted: " + modelId);
        }
    }

    @RequestMapping(
            value = {"/activiti/models/{modelId}/editor/json"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public ObjectNode getModelJSON(@PathVariable String modelId) {
        Model model = this.modelService.getModel(modelId);
        ObjectNode modelNode = this.objectMapper.createObjectNode();
        modelNode.put("modelId", model.getId());
        modelNode.put("name", model.getName());
        modelNode.put("key", model.getKey());
        modelNode.put("description", model.getDescription());
        modelNode.putPOJO("lastUpdated", model.getLastUpdated());
        modelNode.put("lastUpdatedBy", model.getLastUpdatedBy());
        ObjectNode editorJsonNode;
        if (StringUtils.isNotEmpty(model.getModelEditorJson())) {
            try {
                editorJsonNode = (ObjectNode) this.objectMapper.readTree(model.getModelEditorJson());
                editorJsonNode.put("modelType", "model");
                modelNode.put("model", editorJsonNode);
            } catch (Exception var6) {
                log.error("Error reading editor json " + modelId, var6);
                throw new InternalServerErrorException("Error reading editor json " + modelId);
            }
        } else {
            editorJsonNode = this.objectMapper.createObjectNode();
            editorJsonNode.put("id", "canvas");
            editorJsonNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = this.objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorJsonNode.put("modelType", "model");
            modelNode.put("model", editorJsonNode);
        }

        return modelNode;
    }

    @RequestMapping(
            value = {"/activiti/models/{modelId}/editor/json"},
            method = {RequestMethod.POST}
    )
    public ModelRepresentation saveModel(@PathVariable("modelId") String modelId, @RequestBody MultiValueMap<String, String> values) {

        if(values == null) {
            throw  new BadRequestException("values are null");
        }

        long lastUpdated = -1L;
        String lastUpdatedString = (String) values.getFirst("lastUpdated");
        if (lastUpdatedString == null) {
            throw new BadRequestException("Missing lastUpdated date");
        } else {
            try {
                Date readValue = this.objectMapper.getDeserializationConfig().getDateFormat().parse(lastUpdatedString);
                lastUpdated = readValue.getTime();
            } catch (ParseException var12) {
                throw new BadRequestException("Invalid lastUpdated date: '" + lastUpdatedString + "'");
            }

            Model model = this.modelService.getModel(modelId);
            User currentUser = identityService.getCurrentUserObject();
            boolean currentUserIsOwner = model.getLastUpdatedBy().equals(currentUser.getId());
            String resolveAction = (String) values.getFirst("conflictResolveAction");
            if (model.getLastUpdated().getTime() != lastUpdated) {
                String isNewVersionString;
                if ("saveAs".equals(resolveAction)) {
                    isNewVersionString = (String) values.getFirst("saveAs");
                    String json = (String) values.getFirst("json_xml");
                    return this.createNewModel(isNewVersionString, model.getDescription(), model.getModelType(), json);
                } else if ("overwrite".equals(resolveAction)) {
                    return this.updateModel(model, values, false);
                } else if ("newVersion".equals(resolveAction)) {
                    return this.updateModel(model, values, true);
                } else {
                    isNewVersionString = (String) values.getFirst("newversion");
                    if (currentUserIsOwner && "true".equals(isNewVersionString)) {
                        return this.updateModel(model, values, true);
                    } else {
                        ConflictingRequestException exception = new ConflictingRequestException("Process model was updated in the meantime");
                        exception.addCustomData("userFullName", model.getLastUpdatedBy());
                        exception.addCustomData("newVersionAllowed", currentUserIsOwner);
                        throw exception;
                    }
                }
            } else {
                return this.updateModel(model, values, false);
            }
        }
    }

    @RequestMapping(
            value = {"/activiti/models/{modelId}/newversion"},
            method = {RequestMethod.POST}
    )
    public ModelRepresentation importNewVersion(@PathVariable String modelId, @RequestParam("file") MultipartFile file) {
        return super.importNewVersion(modelId, file);
    }

    protected ModelRepresentation updateModel(Model model, MultiValueMap<String, String> values, boolean forceNewVersion) {
        String name = (String) values.getFirst("name");
        String key = (String) values.getFirst("key");
        String description = (String) values.getFirst("description");
        String isNewVersionString = (String) values.getFirst("newversion");
        String newVersionComment = null;
        ModelKeyRepresentation modelKeyInfo = this.modelService.validateModelKey(model, model.getModelType(), key);
        if (modelKeyInfo.isKeyAlreadyExists()) {
            throw new BadRequestException("Model with provided key already exists " + key);
        } else {
            boolean newVersion = false;
            if (forceNewVersion) {
                newVersion = true;
                newVersionComment = (String) values.getFirst("comment");
            } else if (isNewVersionString != null) {
                newVersion = "true".equals(isNewVersionString);
                newVersionComment = (String) values.getFirst("comment");
            }

            String json = (String) values.getFirst("json_xml");

            try {
                model = this.modelService.saveModel(model.getId(), name, key, description, json, newVersion, newVersionComment, identityService.getCurrentUserObject());
                return new ModelRepresentation(model);
            } catch (Exception var13) {
                log.error("Error saving model " + model.getId(), var13);
                throw new BadRequestException("Process model could not be saved " + model.getId());
            }
        }
    }

    protected ModelRepresentation createNewModel(String name, String description, Integer modelType, String editorJson) {
        ModelRepresentation model = new ModelRepresentation();
        model.setName(name);
        model.setDescription(description);
        model.setModelType(modelType);
        Model newModel = this.modelService.createModel(model, editorJson, identityService.getCurrentUserObject());
        return new ModelRepresentation(newModel);
    }
}
