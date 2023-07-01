package com.innov.workflow.activiti.rest.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.model.editor.DecisionTableSaveRepresentation;
import com.innov.workflow.activiti.model.editor.ModelRepresentation;
import com.innov.workflow.activiti.model.editor.decisiontable.DecisionTableRepresentation;
import com.innov.workflow.activiti.service.editor.ActivitiDecisionTableService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping({"/api/activiti/decision-table-models"})
public class DecisionTableResource {
    private static final Logger logger = LoggerFactory.getLogger(DecisionTableResource.class);
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ActivitiDecisionTableService decisionTableService;

    public DecisionTableResource() {
    }

    @RequestMapping(
            value = {"/values"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public List<DecisionTableRepresentation> getDecisionTables(HttpServletRequest request) {
        String[] decisionTableIds = request.getParameterValues("decisionTableId");
        if (decisionTableIds != null && decisionTableIds.length != 0) {
            return this.decisionTableService.getDecisionTables(decisionTableIds);
        } else {
            throw new BadRequestException("No decisionTableId parameter(s) provided in the request");
        }
    }

    @RequestMapping(
            value = {"/{decisionTableId}"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public DecisionTableRepresentation getDecisionTable(@PathVariable String decisionTableId) {
        return this.decisionTableService.getDecisionTable(decisionTableId);
    }

    @RequestMapping(
            value = {"/{decisionTableId}/export"},
            method = {RequestMethod.GET}
    )
    public void exportDecisionTable(HttpServletResponse response, @PathVariable String decisionTableId) {
        this.decisionTableService.exportDecisionTable(response, decisionTableId);
    }

    @RequestMapping(
            value = {"/import-decision-table"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public ModelRepresentation importDecisionTable(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        return this.decisionTableService.importDecisionTable(request, file);
    }

    @RequestMapping(
            value = {"/import-decision-table-text"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public String importDecisionTableText(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        ModelRepresentation decisionTableRepresentation = this.decisionTableService.importDecisionTable(request, file);
        String json = null;

        try {
            json = this.objectMapper.writeValueAsString(decisionTableRepresentation);
            return json;
        } catch (Exception var6) {
            logger.error("Error writing imported decision table json", var6);
            throw new InternalServerErrorException("Error writing imported decision table representation json");
        }
    }

    @RequestMapping(
            value = {"/history/{historyModelId}"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public DecisionTableRepresentation getHistoricDecisionTable(@PathVariable String historyModelId) {
        return this.decisionTableService.getHistoricDecisionTable(historyModelId);
    }

    @RequestMapping(
            value = {"/history/{historyModelId}/export"},
            method = {RequestMethod.GET}
    )
    public void exportHistoricDecisionTable(HttpServletResponse response, @PathVariable String historyModelId) {
        this.decisionTableService.exportHistoricDecisionTable(response, historyModelId);
    }

    @RequestMapping(
            value = {"/{decisionTableId}"},
            method = {RequestMethod.PUT},
            produces = {"application/json"}
    )
    public DecisionTableRepresentation saveDecisionTable(@PathVariable String decisionTableId, @RequestBody DecisionTableSaveRepresentation saveRepresentation) {
        return this.decisionTableService.saveDecisionTable(decisionTableId, saveRepresentation);
    }
}
