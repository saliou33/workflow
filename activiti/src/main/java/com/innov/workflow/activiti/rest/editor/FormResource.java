package com.innov.workflow.activiti.rest.editor;

import com.innov.workflow.activiti.model.editor.FormSaveRepresentation;
import com.innov.workflow.activiti.model.editor.form.FormRepresentation;
import com.innov.workflow.activiti.service.editor.ActivitiFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping({"/app/rest/form-models"})
public class FormResource {
    @Autowired
    protected ActivitiFormService formService;

    public FormResource() {
    }

    @RequestMapping(
            value = {"/{formId}"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public FormRepresentation getForm(@PathVariable String formId) {
        return this.formService.getForm(formId);
    }

    @RequestMapping(
            value = {"/values"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public List<FormRepresentation> getForms(HttpServletRequest request) {
        String[] formIds = request.getParameterValues("formId");
        return this.formService.getForms(formIds);
    }

    @RequestMapping(
            value = {"/{formId}/history/{formHistoryId}"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public FormRepresentation getFormHistory(@PathVariable String formId, @PathVariable String formHistoryId) {
        return this.formService.getFormHistory(formId, formHistoryId);
    }

    @RequestMapping(
            value = {"/{formId}"},
            method = {RequestMethod.PUT},
            produces = {"application/json"}
    )
    public FormRepresentation saveForm(@PathVariable String formId, @RequestBody FormSaveRepresentation saveRepresentation) {
        return this.formService.saveForm(formId, saveRepresentation);
    }
}
