package com.innov.workflow.activiti.rest.editor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ModelBpmnResource extends AbstractModelBpmnResource {
    public ModelBpmnResource() {
    }

    @RequestMapping(
            value = {"/activiti/models/{processModelId}/bpmn20"},
            method = {RequestMethod.GET}
    )
    public void getProcessModelBpmn20Xml(HttpServletResponse response, @PathVariable String processModelId) throws IOException {
        super.getProcessModelBpmn20Xml(response, processModelId);
    }

    @RequestMapping(
            value = {"/activiti/models/{processModelId}/history/{processModelHistoryId}/bpmn20"},
            method = {RequestMethod.GET}
    )
    public void getHistoricProcessModelBpmn20Xml(HttpServletResponse response, @PathVariable String processModelId, @PathVariable String processModelHistoryId) throws IOException {
        super.getHistoricProcessModelBpmn20Xml(response, processModelId, processModelHistoryId);
    }
}
