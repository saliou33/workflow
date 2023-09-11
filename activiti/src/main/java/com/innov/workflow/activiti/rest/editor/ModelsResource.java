package com.innov.workflow.activiti.rest.editor;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.domain.editor.AppDefinition;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.dto.ModelCount;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.editor.ModelKeyRepresentation;
import com.innov.workflow.activiti.model.editor.ModelRepresentation;
import com.innov.workflow.activiti.model.editor.decisiontable.DecisionTableDefinitionRepresentation;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import org.activiti.form.model.FormDefinition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ModelsResource extends AbstractModelsResource {
    private final Logger logger = LoggerFactory.getLogger(ModelsResource.class);

    @Autowired
    protected IdentityService identityService;

    public ModelsResource() {
    }

    @RequestMapping(
            value = {"/activiti/models"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public ResultListDataRepresentation getModels(@RequestParam(required = false) String filter, @RequestParam(required = false) String sort, @RequestParam(required = false) Integer modelType, HttpServletRequest request) {
        return super.getModels(filter, sort, modelType, request);
    }

    @RequestMapping(
            value = {"/activiti/models/count"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public List<ModelCount> getModelsCount(HttpServletRequest request) {
        return getModelsCountByType(identityService.getCurrentUserObject().getId());
    }

    @RequestMapping(
            value = {"/activiti/models-for-app-definition"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public ResultListDataRepresentation getModelsToIncludeInAppDefinition() {
        return super.getModelsToIncludeInAppDefinition();
    }

    @RequestMapping(
            value = {"/activiti/import-process-model"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public ModelRepresentation importProcessModel(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        return super.importProcessModel(request, file);
    }

    @RequestMapping(
            value = {"/activiti/import-process-model/text"},
            method = {RequestMethod.POST}
    )
    public String importProcessModelText(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        ModelRepresentation modelRepresentation = super.importProcessModel(request, file);
        String modelRepresentationJson = null;

        try {
            modelRepresentationJson = this.objectMapper.writeValueAsString(modelRepresentation);
            return modelRepresentationJson;
        } catch (Exception var6) {
            this.logger.error("Error while processing Model representation json", var6);
            throw new InternalServerErrorException("Model Representation could not be saved");
        }
    }

    @RequestMapping(
            value = {"/activiti/models"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public ModelRepresentation createModel(@RequestBody ModelRepresentation modelRepresentation) {
        modelRepresentation.setKey(modelRepresentation.getKey().replaceAll(" ", ""));
        ModelKeyRepresentation modelKeyInfo = this.modelService.validateModelKey(null, modelRepresentation.getModelType(), modelRepresentation.getKey());
        if (modelKeyInfo.isKeyAlreadyExists()) {
            throw new BadRequestException("Provided model key already exists: " + modelRepresentation.getKey());
        } else {
            String json = null;
            if (modelRepresentation.getModelType() != null && modelRepresentation.getModelType().equals(2)) {
                try {
                    json = this.objectMapper.writeValueAsString(new FormDefinition());
                } catch (Exception var15) {
                    this.logger.error("Error creating form model", var15);
                    throw new InternalServerErrorException("Error creating form");
                }
            } else if (modelRepresentation.getModelType() != null && modelRepresentation.getModelType().equals(4)) {
                try {
                    DecisionTableDefinitionRepresentation decisionTableDefinition = new DecisionTableDefinitionRepresentation();
                    String decisionTableDefinitionKey = modelRepresentation.getName().replaceAll(" ", "");
                    decisionTableDefinition.setKey(decisionTableDefinitionKey);
                    json = this.objectMapper.writeValueAsString(decisionTableDefinition);
                } catch (Exception var14) {
                    this.logger.error("Error creating decision table model", var14);
                    throw new InternalServerErrorException("Error creating decision table");
                }
            } else if (modelRepresentation.getModelType() != null && modelRepresentation.getModelType().equals(3)) {
                try {
                    json = this.objectMapper.writeValueAsString(new AppDefinition());
                } catch (Exception var13) {
                    this.logger.error("Error creating app definition", var13);
                    throw new InternalServerErrorException("Error creating app definition");
                }
            } else {
                ObjectNode editorNode = this.objectMapper.createObjectNode();
                editorNode.put("id", "canvas");
                editorNode.put("resourceId", "canvas");
                ObjectNode stencilSetNode = this.objectMapper.createObjectNode();
                stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
                editorNode.put("stencilset", stencilSetNode);
                ObjectNode propertiesNode = this.objectMapper.createObjectNode();
                propertiesNode.put("process_id", modelRepresentation.getKey());
                propertiesNode.put("name", modelRepresentation.getName());
                if (StringUtils.isNotEmpty(modelRepresentation.getDescription())) {
                    propertiesNode.put("documentation", modelRepresentation.getDescription());
                }

                editorNode.put("properties", propertiesNode);
                ArrayNode childShapeArray = this.objectMapper.createArrayNode();
                editorNode.put("childShapes", childShapeArray);
                ObjectNode childNode = this.objectMapper.createObjectNode();
                childShapeArray.add(childNode);
                ObjectNode boundsNode = this.objectMapper.createObjectNode();
                childNode.put("bounds", boundsNode);
                ObjectNode lowerRightNode = this.objectMapper.createObjectNode();
                boundsNode.put("lowerRight", lowerRightNode);
                lowerRightNode.put("x", 130);
                lowerRightNode.put("y", 193);
                ObjectNode upperLeftNode = this.objectMapper.createObjectNode();
                boundsNode.put("upperLeft", upperLeftNode);
                upperLeftNode.put("x", 100);
                upperLeftNode.put("y", 163);
                childNode.put("childShapes", this.objectMapper.createArrayNode());
                childNode.put("dockers", this.objectMapper.createArrayNode());
                childNode.put("outgoing", this.objectMapper.createArrayNode());
                childNode.put("resourceId", "startEvent1");
                ObjectNode stencilNode = this.objectMapper.createObjectNode();
                childNode.put("stencil", stencilNode);
                stencilNode.put("id", "StartNoneEvent");
                json = editorNode.toString();
            }

            Model newModel = this.modelService.createModel(modelRepresentation, json, identityService.getCurrentUserObject());
            return new ModelRepresentation(newModel);
        }
    }

    @RequestMapping(
            value = {"/activiti/models/{modelId}/clone"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public ModelRepresentation duplicateModel(@PathVariable String modelId, @RequestBody ModelRepresentation modelRepresentation) {
        String json = null;
        Model model = null;
        if (modelId != null) {
            model = this.modelService.getModel(modelId);
            json = model.getModelEditorJson();
        }

        if (model == null) {
            throw new InternalServerErrorException("Error duplicating model : Unknown original model");
        } else {
            if ((modelRepresentation.getModelType() == null || !modelRepresentation.getModelType().equals(2)) && (modelRepresentation.getModelType() == null || !modelRepresentation.getModelType().equals(3))) {
                ObjectNode editorNode;
                if (modelRepresentation.getModelType() != null && modelRepresentation.getModelType().equals(4)) {
                    editorNode = null;

                    try {
                        editorNode = (ObjectNode) this.objectMapper.readTree(json);
                        json = this.objectMapper.writeValueAsString(editorNode);
                    } catch (Exception var10) {
                        this.logger.error("Error creating decision table model", var10);
                        throw new InternalServerErrorException("Error creating decision table");
                    }
                } else {
                    editorNode = null;

                    try {
                        ObjectNode editorJsonNode = (ObjectNode) this.objectMapper.readTree(json);
                        editorNode = this.deleteEmbededReferencesFromBPMNModel(editorJsonNode);
                        ObjectNode propertiesNode = (ObjectNode) editorNode.get("properties");
                        String processId = modelRepresentation.getName().replaceAll(" ", "");
                        propertiesNode.put("process_id", processId);
                        propertiesNode.put("name", modelRepresentation.getName());
                        if (StringUtils.isNotEmpty(modelRepresentation.getDescription())) {
                            propertiesNode.put("documentation", modelRepresentation.getDescription());
                        }

                        editorNode.put("properties", propertiesNode);
                    } catch (IOException var9) {
                        var9.printStackTrace();
                    }

                    if (editorNode != null) {
                        json = editorNode.toString();
                    }
                }
            }

            Model newModel = this.modelService.createModel(modelRepresentation, json, identityService.getCurrentUserObject());
            byte[] imageBytes = model.getThumbnail();
            newModel = this.modelService.saveModel(newModel, newModel.getModelEditorJson(), imageBytes, false, newModel.getComment(), identityService.getCurrentUserObject());
            return new ModelRepresentation(newModel);
        }
    }

    protected ObjectNode deleteEmbededReferencesFromBPMNModel(ObjectNode editorJsonNode) {
        try {
            this.internalDeleteNodeByNameFromBPMNModel(editorJsonNode, "formreference");
            this.internalDeleteNodeByNameFromBPMNModel(editorJsonNode, "subprocessreference");
            return editorJsonNode;
        } catch (Exception var3) {
            throw new InternalServerErrorException("Cannot delete the external references");
        }
    }

    protected ObjectNode deleteEmbededReferencesFromStepModel(ObjectNode editorJsonNode) {
        try {
            JsonNode startFormNode = editorJsonNode.get("startForm");
            if (startFormNode != null) {
                editorJsonNode.remove("startForm");
            }

            this.internalDeleteNodeByNameFromStepModel(editorJsonNode.get("steps"), "formDefinition");
            this.internalDeleteNodeByNameFromStepModel(editorJsonNode.get("steps"), "subProcessDefinition");
            return editorJsonNode;
        } catch (Exception var3) {
            throw new InternalServerErrorException("Cannot delete the external references");
        }
    }

    protected void internalDeleteNodeByNameFromBPMNModel(JsonNode editorJsonNode, String propertyName) {
        JsonNode childShapesNode = editorJsonNode.get("childShapes");
        if (childShapesNode != null && childShapesNode.isArray()) {
            ArrayNode childShapesArrayNode = (ArrayNode) childShapesNode;
            Iterator i$ = childShapesArrayNode.iterator();

            while (i$.hasNext()) {
                JsonNode childShapeNode = (JsonNode) i$.next();
                ObjectNode properties = (ObjectNode) childShapeNode.get("properties");
                if (properties != null && properties.has(propertyName)) {
                    JsonNode propertyNode = properties.get(propertyName);
                    if (propertyNode != null) {
                        properties.remove(propertyName);
                    }
                }

                if (childShapeNode.has("childShapes")) {
                    this.internalDeleteNodeByNameFromBPMNModel(childShapeNode, propertyName);
                }
            }
        }

    }

    private void internalDeleteNodeByNameFromStepModel(JsonNode stepsNode, String propertyName) {
        if (stepsNode != null && stepsNode.isArray()) {
            Iterator i$ = stepsNode.iterator();

            while (true) {
                ObjectNode stepNode;
                do {
                    if (!i$.hasNext()) {
                        return;
                    }

                    JsonNode jsonNode = (JsonNode) i$.next();
                    stepNode = (ObjectNode) jsonNode;
                    if (stepNode.has(propertyName)) {
                        JsonNode propertyNode = stepNode.get(propertyName);
                        if (propertyNode != null) {
                            stepNode.remove(propertyName);
                        }
                    }

                    if (stepNode.has("steps")) {
                        this.internalDeleteNodeByNameFromStepModel(stepNode.get("steps"), propertyName);
                    }

                    if (stepNode.has("overdueSteps")) {
                        this.internalDeleteNodeByNameFromStepModel(stepNode.get("overdueSteps"), propertyName);
                    }
                } while (!stepNode.has("choices"));

                ArrayNode choicesArrayNode = (ArrayNode) stepNode.get("choices");
                Iterator j$ = choicesArrayNode.iterator();

                while (j$.hasNext()) {
                    JsonNode choiceNode = (JsonNode) j$.next();
                    if (choiceNode.has("steps")) {
                        this.internalDeleteNodeByNameFromStepModel(choiceNode.get("steps"), propertyName);
                    }
                }
            }
        }
    }
}
