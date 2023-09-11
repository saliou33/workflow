package com.innov.workflow.activiti.service.editor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.domain.editor.ModelHistory;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.editor.DecisionTableSaveRepresentation;
import com.innov.workflow.activiti.model.editor.ModelKeyRepresentation;
import com.innov.workflow.activiti.model.editor.ModelRepresentation;
import com.innov.workflow.activiti.model.editor.decisiontable.DecisionTableDefinitionRepresentation;
import com.innov.workflow.activiti.model.editor.decisiontable.DecisionTableRepresentation;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.activiti.util.XmlUtil;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.dmn.model.DmnDefinition;
import org.activiti.dmn.xml.converter.DmnXMLConverter;
import org.activiti.editor.dmn.converter.DmnJsonConverter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ActivitiDecisionTableService extends BaseActivitiModelService {
    protected static final int MIN_FILTER_LENGTH = 1;
    private static final Logger logger = LoggerFactory.getLogger(ActivitiDecisionTableService.class);
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected IdentityService identityService;
    protected DmnJsonConverter dmnJsonConverter = new DmnJsonConverter();
    protected DmnXMLConverter dmnXmlConverter = new DmnXMLConverter();

    public ActivitiDecisionTableService() {
    }

    public List<DecisionTableRepresentation> getDecisionTables(String[] decisionTableIds) {
        List<DecisionTableRepresentation> decisionTableRepresentations = new ArrayList();
        String[] arr$ = decisionTableIds;
        int len$ = decisionTableIds.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            String decisionTableId = arr$[i$];
            Model model = this.getModel(decisionTableId, true, false);
            DecisionTableRepresentation decisionTableRepresentation = this.createDecisionTableRepresentation(model);
            decisionTableRepresentations.add(decisionTableRepresentation);
        }

        return decisionTableRepresentations;
    }

    public ResultListDataRepresentation getDecisionTables(String filter) {
        String validFilter = this.makeValidFilterText(filter);
        List<Model> models = null;
        if (validFilter != null) {
            models = this.modelRepository.findModelsByModelType(4, validFilter);
        } else {
            models = this.modelRepository.findModelsByModelType(4);
        }

        List<DecisionTableRepresentation> reps = new ArrayList();
        Iterator i$ = models.iterator();

        while (i$.hasNext()) {
            Model model = (Model) i$.next();
            reps.add(new DecisionTableRepresentation(model));
        }

        ResultListDataRepresentation result = new ResultListDataRepresentation(reps);
        result.setTotal((long) models.size());
        return result;
    }

    public void exportDecisionTable(HttpServletResponse response, String decisionTableId) {
        this.exportDecisionTable(response, this.getModel(decisionTableId, true, false));
    }

    public void exportHistoricDecisionTable(HttpServletResponse response, String modelHistoryId) {
        ModelHistory modelHistory = this.modelHistoryRepository.findById(modelHistoryId).orElse(null);
        this.getModel(modelHistory.getModelId(), true, false);
        this.exportDecisionTable(response, modelHistory);
    }

    public void exportDecisionTableHistory(HttpServletResponse response, String decisionTableId) {
        this.exportDecisionTable(response, this.getModel(decisionTableId, true, false));
    }

    protected void exportDecisionTable(HttpServletResponse response, AbstractModel decisionTableModel) {
        DecisionTableRepresentation decisionTableRepresentation = this.getDecisionTableRepresentation(decisionTableModel);

        try {
            JsonNode editorJsonNode = this.objectMapper.readTree(decisionTableModel.getModelEditorJson());
            String fileName = URLEncoder.encode(decisionTableRepresentation.getName(), "UTF-8").replaceAll("\\+", "%20") + ".dmn";
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
            ServletOutputStream servletOutputStream = response.getOutputStream();
            response.setContentType("application/xml");
            DmnDefinition dmnDefinition = this.dmnJsonConverter.convertToDmn(editorJsonNode, decisionTableModel.getId(), decisionTableModel.getVersion(), decisionTableModel.getLastUpdated());
            byte[] xmlBytes = this.dmnXmlConverter.convertToXML(dmnDefinition);
            BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(xmlBytes));
            byte[] buffer = new byte[8096];

            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    servletOutputStream.flush();
                    servletOutputStream.close();
                    return;
                }

                servletOutputStream.write(buffer, 0, count);
            }
        } catch (Exception var12) {
            logger.error("Could not export decision table model", var12);
            throw new InternalServerErrorException("Could not export decision table model");
        }
    }

    public ModelRepresentation importDecisionTable(HttpServletRequest request, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && (fileName.endsWith(".dmn") || fileName.endsWith(".xml"))) {
            try {
                XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
                InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
                DmnDefinition dmnDefinition = this.dmnXmlConverter.convertToDmnModel(xtr);
                ObjectNode editorJsonNode = this.dmnJsonConverter.convertToJson(dmnDefinition);
                editorJsonNode.remove("id");
                ModelRepresentation modelRepresentation = new ModelRepresentation();
                modelRepresentation.setKey(dmnDefinition.getDecisions().get(0).getId());
                modelRepresentation.setName(dmnDefinition.getName());
                modelRepresentation.setDescription(dmnDefinition.getDescription());
                modelRepresentation.setModelType(4);
                Model model = this.modelService.createModel(modelRepresentation, editorJsonNode.toString(), identityService.getCurrentUserObject());
                return new ModelRepresentation(model);
            } catch (Exception var11) {
                logger.error("Could not import decision table model", var11);
                throw new InternalServerErrorException("Could not import decision table model");
            }
        } else {
            throw new BadRequestException("Invalid file name, only .dmn or .xml files are supported not " + fileName);
        }
    }

    protected String makeValidFilterText(String filterText) {
        String validFilter = null;
        if (filterText != null) {
            String trimmed = StringUtils.trim(filterText);
            if (trimmed.length() >= 1) {
                validFilter = "%" + trimmed.toLowerCase() + "%";
            }
        }

        return validFilter;
    }

    public Model getDecisionTableModel(String decisionTableId) {
        return this.getModel(decisionTableId, true, false);
    }

    public DecisionTableRepresentation getDecisionTable(String decisionTableId) {
        return this.createDecisionTableRepresentation(this.getDecisionTableModel(decisionTableId));
    }

    public DecisionTableRepresentation getDecisionTableRepresentation(AbstractModel decisionTableModel) {
        return this.createDecisionTableRepresentation(decisionTableModel);
    }

    public DecisionTableRepresentation getHistoricDecisionTable(String modelHistoryId) {
        ModelHistory modelHistory = this.modelHistoryRepository.findById(modelHistoryId).orElse(null);
        this.getModel(modelHistory.getModelId(), true, false);
        return this.createDecisionTableRepresentation(modelHistory);
    }

    protected DecisionTableRepresentation createDecisionTableRepresentation(AbstractModel model) {
        DecisionTableDefinitionRepresentation decisionTableDefinitionRepresentation = null;

        try {
            decisionTableDefinitionRepresentation = this.objectMapper.readValue(model.getModelEditorJson(), DecisionTableDefinitionRepresentation.class);
        } catch (Exception var4) {
            logger.error("Error deserializing decision table", var4);
            throw new InternalServerErrorException("Could not deserialize decision table definition");
        }

        DecisionTableRepresentation result = new DecisionTableRepresentation(model);
        result.setDecisionTableDefinition(decisionTableDefinitionRepresentation);
        return result;
    }

    public DecisionTableRepresentation saveDecisionTable(String decisionTableId, DecisionTableSaveRepresentation saveRepresentation) {
        User user = identityService.getCurrentUserObject();
        Model model = this.getModel(decisionTableId, false, false);
        String decisionKey = saveRepresentation.getDecisionTableRepresentation().getKey();
        ModelKeyRepresentation modelKeyInfo = this.modelService.validateModelKey(model, model.getModelType(), decisionKey);
        if (modelKeyInfo.isKeyAlreadyExists()) {
            throw new BadRequestException("Provided model key already exists: " + decisionKey);
        } else {
            model.setName(saveRepresentation.getDecisionTableRepresentation().getName());
            model.setKey(decisionKey);
            model.setDescription(saveRepresentation.getDecisionTableRepresentation().getDescription());
            String editorJson = null;

            try {
                editorJson = this.objectMapper.writeValueAsString(saveRepresentation.getDecisionTableRepresentation().getDecisionTableDefinition());
            } catch (Exception var11) {
                logger.error("Error while processing decision table json", var11);
                throw new InternalServerErrorException("Decision table could not be saved " + decisionTableId);
            }

            String filteredImageString = saveRepresentation.getDecisionTableImageBase64().replace("data:image/png;base64,", "");
            byte[] imageBytes = Base64.decodeBase64(filteredImageString);
            model = this.modelService.saveModel(model, editorJson, imageBytes, saveRepresentation.isNewVersion(), saveRepresentation.getComment(), user);
            DecisionTableRepresentation result = new DecisionTableRepresentation(model);
            result.setDecisionTableDefinition(saveRepresentation.getDecisionTableRepresentation().getDecisionTableDefinition());
            return result;
        }
    }
}
