package com.innov.workflow.activiti.rest.editor;

import com.innov.workflow.activiti.domain.editor.ModelHistory;
import com.innov.workflow.activiti.model.common.BaseRestActionRepresentation;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.editor.ModelRepresentation;
import com.innov.workflow.activiti.model.editor.ReviveModelResultRepresentation;
import com.innov.workflow.activiti.old.service.IdentityService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class ModelHistoryResource extends AbstractModelHistoryResource {

    @Autowired
    IdentityService identityService;

    public ModelHistoryResource() {
    }

    @RequestMapping(
            value = {"/rest/models/{modelId}/history"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public ResultListDataRepresentation getModelHistoryCollection(@PathVariable String modelId, @RequestParam(value = "includeLatestVersion", required = false) Boolean includeLatestVersion) {
        return super.getModelHistoryCollection(modelId, includeLatestVersion);
    }

    @RequestMapping(
            value = {"/rest/models/{modelId}/history/{modelHistoryId}"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public ModelRepresentation getProcessModelHistory(@PathVariable String modelId, @PathVariable String modelHistoryId) {
        return super.getProcessModelHistory(modelId, modelHistoryId);
    }

    @RequestMapping(
            value = {"/rest/models/{modelId}/history/{modelHistoryId}"},
            method = {RequestMethod.POST},
            produces = {"application/json"}
    )
    public ReviveModelResultRepresentation executeProcessModelHistoryAction(@PathVariable String modelId, @PathVariable String modelHistoryId, @RequestBody(required = true) BaseRestActionRepresentation action) {
        ModelHistory modelHistory = this.modelService.getModelHistory(modelId, modelHistoryId);
        if ("useAsNewVersion".equals(action.getAction())) {
            return this.modelService.reviveProcessModelHistory(modelHistory, identityService.getCurrentUserObject(), action.getComment());
        } else {
            throw new BadRequestException("Invalid action to execute on model history " + modelHistoryId + ": " + action.getAction());
        }
    }
}


