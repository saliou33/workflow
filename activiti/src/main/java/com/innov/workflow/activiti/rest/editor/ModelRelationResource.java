package com.innov.workflow.activiti.rest.editor;

import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.domain.editor.ModelInformation;
import com.innov.workflow.activiti.service.editor.ModelRelationService;
import com.innov.workflow.activiti.service.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app")
public class ModelRelationResource extends AbstractModelResource {
    @Autowired
    private ModelRelationService modelRelationService;

    public ModelRelationResource() {
    }

    @RequestMapping(
            value = {"/rest/models/{modelId}/parent-relations"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public List<ModelInformation> getModelRelations(@PathVariable String modelId) {
        Model model = this.modelService.getModel(modelId);
        if (model == null) {
            throw new NotFoundException();
        } else {
            return this.modelRelationService.findParentModels(modelId);
        }
    }
}
