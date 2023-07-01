package com.innov.workflow.activiti.service.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.domain.editor.ModelHistory;
import com.innov.workflow.activiti.model.editor.FormSaveRepresentation;
import com.innov.workflow.activiti.model.editor.ModelKeyRepresentation;
import com.innov.workflow.activiti.model.editor.form.FormRepresentation;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.form.model.FormDefinition;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivitiFormService {
    private static final Logger logger = LoggerFactory.getLogger(ActivitiFormService.class);
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected IdentityService identityService;

    public ActivitiFormService() {
    }

    public FormRepresentation getForm(String formId) {
        Model model = this.modelService.getModel(formId);
        FormRepresentation form = this.createFormRepresentation(model);
        return form;
    }

    public FormRepresentation getFormHistory(String formId, String formHistoryId) {
        ModelHistory model = this.modelService.getModelHistory(formId, formHistoryId);
        FormRepresentation form = this.createFormRepresentation(model);
        return form;
    }

    public List<FormRepresentation> getForms(String[] formIds) {
        List<FormRepresentation> formRepresentations = new ArrayList();
        if (formIds != null && formIds.length != 0) {
            String[] arr$ = formIds;
            int len$ = formIds.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String formId = arr$[i$];
                Model model = this.modelService.getModel(formId);
                FormRepresentation form = this.createFormRepresentation(model);
                formRepresentations.add(form);
            }

            return formRepresentations;
        } else {
            throw new BadRequestException("No formIds provided in the request");
        }
    }

    public FormRepresentation saveForm(String formId, FormSaveRepresentation saveRepresentation) {
        User user = identityService.getCurrentUserObject();
        Model model = this.modelService.getModel(formId);
        String formKey = saveRepresentation.getFormRepresentation().getKey();
        ModelKeyRepresentation modelKeyInfo = this.modelService.validateModelKey(model, model.getModelType(), formKey);
        if (modelKeyInfo.isKeyAlreadyExists()) {
            throw new BadRequestException("Model with provided key already exists " + formKey);
        } else {
            model.setName(saveRepresentation.getFormRepresentation().getName());
            model.setKey(formKey);
            model.setDescription(saveRepresentation.getFormRepresentation().getDescription());
            String editorJson = null;

            try {
                editorJson = this.objectMapper.writeValueAsString(saveRepresentation.getFormRepresentation().getFormDefinition());
            } catch (Exception var11) {
                logger.error("Error while processing form json", var11);
                throw new InternalServerErrorException("Form could not be saved " + formId);
            }

            String filteredImageString = saveRepresentation.getFormImageBase64().replace("data:image/png;base64,", "");
            byte[] imageBytes = Base64.decodeBase64(filteredImageString);
            model = this.modelService.saveModel(model, editorJson, imageBytes, saveRepresentation.isNewVersion(), saveRepresentation.getComment(), user);
            FormRepresentation result = new FormRepresentation(model);
            result.setFormDefinition(saveRepresentation.getFormRepresentation().getFormDefinition());
            return result;
        }
    }

    protected FormRepresentation createFormRepresentation(AbstractModel model) {
        FormDefinition formDefinition = null;

        try {
            formDefinition = (FormDefinition) this.objectMapper.readValue(model.getModelEditorJson(), FormDefinition.class);
        } catch (Exception var4) {
            logger.error("Error deserializing form", var4);
            throw new InternalServerErrorException("Could not deserialize form definition");
        }

        FormRepresentation result = new FormRepresentation(model);
        result.setFormDefinition(formDefinition);
        return result;
    }
}
