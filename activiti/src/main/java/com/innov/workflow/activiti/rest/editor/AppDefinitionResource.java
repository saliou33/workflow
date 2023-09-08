package com.innov.workflow.activiti.rest.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.domain.editor.*;
import com.innov.workflow.activiti.model.editor.AppDefinitionPublishRepresentation;
import com.innov.workflow.activiti.model.editor.AppDefinitionRepresentation;
import com.innov.workflow.activiti.model.editor.AppDefinitionSaveRepresentation;
import com.innov.workflow.activiti.model.editor.AppDefinitionUpdateResultRepresentation;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.editor.AppDefinitionExportService;
import com.innov.workflow.activiti.service.editor.AppDefinitionImportService;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.core.domain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class AppDefinitionResource {
    private static final Logger logger = LoggerFactory.getLogger(AppDefinitionResource.class);
    @Autowired
    protected AppDefinitionExportService appDefinitionExportService;
    @Autowired
    protected AppDefinitionImportService appDefinitionImportService;
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected IdentityService identityService;

    public AppDefinitionResource() {
    }

    @RequestMapping(
            value = {"/activiti/app-definitions/{modelId}"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public AppDefinitionRepresentation getAppDefinition(@PathVariable("modelId") String modelId) {
        Model model = this.modelService.getModel(modelId);
        return this.createAppDefinitionRepresentation(model);
    }



    @RequestMapping(
            value = {"/activiti/app-definitions/{modelId}/history/{modelHistoryId}"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public AppDefinitionRepresentation getAppDefinitionHistory(@PathVariable String modelId, @PathVariable String modelHistoryId) {
        ModelHistory model = this.modelService.getModelHistory(modelId, modelHistoryId);
        return this.createAppDefinitionRepresentation(model);
    }

    @RequestMapping(
            value = {"/activiti/app-definitions/{modelId}"},
            method = {RequestMethod.PUT},
            produces = {"application/json"}
    )
    public AppDefinitionUpdateResultRepresentation updateAppDefinition(@PathVariable("modelId") String modelId, @RequestBody AppDefinitionSaveRepresentation updatedModel) {
        AppDefinitionUpdateResultRepresentation result = new AppDefinitionUpdateResultRepresentation();
        User user = identityService.getCurrentUserObject();
        Model model = this.modelService.getModel(modelId);
        model.setName(updatedModel.getAppDefinition().getName());
        model.setKey(updatedModel.getAppDefinition().getKey());
        model.setDescription(updatedModel.getAppDefinition().getDescription());
        String editorJson = null;

        try {
            editorJson = this.objectMapper.writeValueAsString(updatedModel.getAppDefinition().getDefinition());
        } catch (Exception var8) {
            logger.error("Error while processing app definition json " + modelId, var8);
            throw new InternalServerErrorException("App definition could not be saved " + modelId);
        }

        model = this.modelService.saveModel(model, editorJson, null, false, null, user);
        if (updatedModel.isPublish()) {
            return this.appDefinitionImportService.publishAppDefinition(modelId, new AppDefinitionPublishRepresentation(null, updatedModel.getForce()));
        } else {
            AppDefinitionRepresentation appDefinition = new AppDefinitionRepresentation(model);
            appDefinition.setDefinition(updatedModel.getAppDefinition().getDefinition());
            result.setAppDefinition(appDefinition);
            return result;
        }
    }

    @RequestMapping(
            value = {"/activiti/app-definitions/{modelId}/publish"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public AppDefinitionUpdateResultRepresentation publishAppDefinition(@PathVariable("modelId") String modelId, @RequestBody AppDefinitionPublishRepresentation publishModel) {
        return this.appDefinitionImportService.publishAppDefinition(modelId, publishModel);
    }

    @RequestMapping(
            value = {"/activiti/app-definitions/{modelId}/export"},
            method = {RequestMethod.GET}
    )
    public void exportAppDefinition(HttpServletResponse response, @PathVariable String modelId) throws IOException {
        this.appDefinitionExportService.exportAppDefinition(response, modelId);
    }

    @Transactional
    @RequestMapping(
            value = {"/activiti/app-definitions/{modelId}/import"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public AppDefinitionRepresentation importAppDefinition(HttpServletRequest request, @PathVariable String modelId, @RequestParam("file") MultipartFile file) {
        return this.appDefinitionImportService.importAppDefinitionNewVersion(request, file, modelId);
    }

    @Transactional
    @RequestMapping(
            value = {"/activiti/app-definitions/{modelId}/text/import"},
            method = {RequestMethod.POST}
    )
    public String importAppDefinitionText(HttpServletRequest request, @PathVariable String modelId, @RequestParam("file") MultipartFile file) {
        AppDefinitionRepresentation appDefinitionRepresentation = this.appDefinitionImportService.importAppDefinitionNewVersion(request, file, modelId);
        String appDefinitionRepresentationJson = null;

        try {
            appDefinitionRepresentationJson = this.objectMapper.writeValueAsString(appDefinitionRepresentation);
            return appDefinitionRepresentationJson;
        } catch (Exception var7) {
            logger.error("Error while App Definition representation json", var7);
            throw new InternalServerErrorException("App definition could not be saved");
        }
    }

    @Transactional
    @RequestMapping(
            value = {"/activiti/app-definitions/import"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public AppDefinitionRepresentation importAppDefinition(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        return this.appDefinitionImportService.importAppDefinition(request, file);
    }

    @Transactional
    @RequestMapping(
            value = {"/activiti/app-definitions/text/import"},
            method = {RequestMethod.POST}
    )
    public String importAppDefinitionText(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        AppDefinitionRepresentation appDefinitionRepresentation = this.appDefinitionImportService.importAppDefinition(request, file);
        String appDefinitionRepresentationJson = null;

        try {
            appDefinitionRepresentationJson = this.objectMapper.writeValueAsString(appDefinitionRepresentation);
            return appDefinitionRepresentationJson;
        } catch (Exception var6) {
            logger.error("Error while App Definition representation json", var6);
            throw new InternalServerErrorException("App definition could not be saved");
        }
    }

    protected AppDefinitionRepresentation createAppDefinitionRepresentation(AbstractModel model) {
        AppDefinition appDefinition;

        try {
            appDefinition = this.objectMapper.readValue(model.getModelEditorJson(), AppDefinition.class);
        } catch (Exception var4) {
            logger.error("Error deserializing app " + model.getId(), var4);
            throw new InternalServerErrorException("Could not deserialize app definition");
        }

        for(AppModelDefinition m: appDefinition.getModels()) {
            m.setKey(modelService.getModel(m.getId()).getKey());
        }

        AppDefinitionRepresentation result = new AppDefinitionRepresentation(model);
        result.setDefinition(appDefinition);
        return result;
    }
}
