package com.innov.workflow.activiti.service.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.domain.editor.AppDefinition;
import com.innov.workflow.activiti.domain.editor.AppModelDefinition;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.model.editor.AppDefinitionRepresentation;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.repository.editor.ModelRepository;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class AppDefinitionExportService {
    private static final Logger logger = LoggerFactory.getLogger(AppDefinitionExportService.class);
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected ModelRepository modelRepository;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected IdentityService identityService;
    protected BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();

    public AppDefinitionExportService() {
    }

    public void exportAppDefinition(HttpServletResponse response, String modelId) throws IOException {
        if (modelId == null) {
            throw new BadRequestException("No application definition id provided");
        } else {
            Model appModel = this.modelService.getModel(modelId);
            AppDefinitionRepresentation appRepresentation = this.createAppDefinitionRepresentation(appModel);
            this.createAppDefinitionZip(response, appModel, appRepresentation, identityService.getCurrentUserObject());
        }
    }

    protected void createAppDefinitionZip(HttpServletResponse response, Model appModel, AppDefinitionRepresentation appDefinition, User user) {
        response.setHeader("Content-Disposition", "attachment; filename=" + appDefinition.getName() + ".zip");

        try {
            ServletOutputStream servletOutputStream = response.getOutputStream();
            response.setContentType("application/zip");
            ZipOutputStream zipOutputStream = new ZipOutputStream(servletOutputStream);
            this.createZipEntry(zipOutputStream, appModel.getName() + ".json", this.createModelEntryJson(appModel));
            List<AppModelDefinition> modelDefinitions = appDefinition.getDefinition().getModels();
            if (CollectionUtils.isNotEmpty(modelDefinitions)) {
                Map<String, Model> formMap = new HashMap();
                Map<String, Model> decisionTableMap = new HashMap();
                Iterator i$ = modelDefinitions.iterator();

                while (i$.hasNext()) {
                    AppModelDefinition modelDef = (AppModelDefinition) i$.next();
                    Model model = this.modelService.getModel(modelDef.getId());
                    List<Model> referencedModels = this.modelRepository.findModelsByParentModelId(model.getId());
                    Iterator j$ = referencedModels.iterator();

                    while (j$.hasNext()) {
                        Model childModel = (Model) j$.next();
                        if (2 == childModel.getModelType()) {
                            formMap.put(childModel.getId(), childModel);
                        } else if (4 == childModel.getModelType()) {
                            decisionTableMap.put(childModel.getId(), childModel);
                        }
                    }

                    this.createZipEntries(model, "bpmn-models", zipOutputStream);
                }

                i$ = formMap.values().iterator();

                Model decisionTableModel;
                while (i$.hasNext()) {
                    decisionTableModel = (Model) i$.next();
                    this.createZipEntries(decisionTableModel, "form-models", zipOutputStream);
                }

                i$ = decisionTableMap.values().iterator();

                while (i$.hasNext()) {
                    decisionTableModel = (Model) i$.next();
                    this.createZipEntries(decisionTableModel, "decision-table-models", zipOutputStream);
                }
            }

            zipOutputStream.close();
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (Exception var16) {
            logger.error("Could not generate app definition zip archive", var16);
            throw new InternalServerErrorException("Could not generate app definition zip archive");
        }
    }

    protected void createZipEntries(Model model, String directoryName, ZipOutputStream zipOutputStream) throws Exception {
        this.createZipEntry(zipOutputStream, directoryName + "/" + model.getKey() + ".json", this.createModelEntryJson(model));
        if (model.getThumbnail() != null) {
            this.createZipEntry(zipOutputStream, directoryName + "/" + model.getKey() + ".png", model.getThumbnail());
        }

    }

    protected String createModelEntryJson(Model model) {
        ObjectNode modelJson = this.objectMapper.createObjectNode();
        modelJson.put("id", model.getId());
        modelJson.put("name", model.getName());
        modelJson.put("key", model.getKey());
        modelJson.put("description", model.getDescription());

        try {
            modelJson.put("editorJson", this.objectMapper.readTree(model.getModelEditorJson()));
        } catch (Exception var4) {
            logger.error("Error exporting model json for id " + model.getId(), var4);
            throw new InternalServerErrorException("Error exporting model json for id " + model.getId());
        }

        return modelJson.toString();
    }

    protected AppDefinitionRepresentation createAppDefinitionRepresentation(AbstractModel model) {
        AppDefinition appDefinition = null;

        try {
            appDefinition = this.objectMapper.readValue(model.getModelEditorJson(), AppDefinition.class);
        } catch (Exception var4) {
            logger.error("Error deserializing app " + model.getId(), var4);
            throw new InternalServerErrorException("Could not deserialize app definition");
        }

        AppDefinitionRepresentation result = new AppDefinitionRepresentation(model);
        result.setDefinition(appDefinition);
        return result;
    }

    protected void createZipEntry(ZipOutputStream zipOutputStream, String filename, String content) throws Exception {
        this.createZipEntry(zipOutputStream, filename, content.getBytes(StandardCharsets.UTF_8));
    }

    protected void createZipEntry(ZipOutputStream zipOutputStream, String filename, byte[] content) throws Exception {
        ZipEntry entry = new ZipEntry(filename);
        zipOutputStream.putNextEntry(entry);
        zipOutputStream.write(content);
        zipOutputStream.closeEntry();
    }
}