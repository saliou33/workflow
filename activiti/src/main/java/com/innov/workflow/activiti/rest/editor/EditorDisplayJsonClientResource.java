package com.innov.workflow.activiti.rest.editor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.domain.editor.ModelHistory;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.editor.BpmnDisplayJsonConverter;
import org.activiti.bpmn.model.GraphicInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class EditorDisplayJsonClientResource {
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected BpmnDisplayJsonConverter bpmnDisplayJsonConverter;
    protected ObjectMapper objectMapper = new ObjectMapper();

    public EditorDisplayJsonClientResource() {
    }

    @RequestMapping(
            value = {"/rest/models/{processModelId}/model-json"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public JsonNode getModelJSON(@PathVariable String processModelId) {
        ObjectNode displayNode = this.objectMapper.createObjectNode();
        Model model = this.modelService.getModel(processModelId);
        this.bpmnDisplayJsonConverter.processProcessElements(model, displayNode, new GraphicInfo());
        return displayNode;
    }

    @RequestMapping(
            value = {"/rest/models/{processModelId}/history/{processModelHistoryId}/model-json"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public JsonNode getModelHistoryJSON(@PathVariable String processModelId, @PathVariable String processModelHistoryId) {
        ObjectNode displayNode = this.objectMapper.createObjectNode();
        ModelHistory model = this.modelService.getModelHistory(processModelId, processModelHistoryId);
        this.bpmnDisplayJsonConverter.processProcessElements(model, displayNode, new GraphicInfo());
        return displayNode;
    }
}
