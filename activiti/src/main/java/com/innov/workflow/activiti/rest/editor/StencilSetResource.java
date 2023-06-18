package com.innov.workflow.activiti.rest.editor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class StencilSetResource {
    private final Logger log = LoggerFactory.getLogger(StencilSetResource.class);
    @Autowired
    protected ObjectMapper objectMapper;

    public StencilSetResource() {
    }

    @RequestMapping(
            value = {"/rest/stencil-sets/editor"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public JsonNode getStencilSetForEditor() {
        try {
            JsonNode stencilNode = this.objectMapper.readTree(this.getClass().getClassLoader().getResourceAsStream("stencilset_bpmn.json"));
            return stencilNode;
        } catch (Exception var2) {
            this.log.error("Error reading bpmn stencil set json", var2);
            throw new InternalServerErrorException("Error reading bpmn stencil set json");
        }
    }
}
