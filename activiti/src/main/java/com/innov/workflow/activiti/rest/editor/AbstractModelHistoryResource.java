package com.innov.workflow.activiti.rest.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.domain.editor.ModelHistory;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.editor.ModelRepresentation;
import com.innov.workflow.activiti.repository.editor.ModelHistoryRepository;
import com.innov.workflow.activiti.service.api.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
public class AbstractModelHistoryResource {
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected ModelHistoryRepository modelHistoryRepository;
    @Autowired
    protected ObjectMapper objectMapper;

    public AbstractModelHistoryResource() {
    }

    public ResultListDataRepresentation getModelHistoryCollection(String modelId, Boolean includeLatestVersion) {
        Model model = this.modelService.getModel(modelId);
        List<ModelHistory> history = this.modelHistoryRepository.findByModelIdAndRemovalDateIsNullOrderByVersionDesc(model.getId());
        ResultListDataRepresentation result = new ResultListDataRepresentation();
        List<ModelRepresentation> representations = new ArrayList();
        if (Boolean.TRUE.equals(includeLatestVersion)) {
            representations.add(new ModelRepresentation(model));
        }

        if (history.size() > 0) {
            Iterator i$ = history.iterator();

            while (i$.hasNext()) {
                ModelHistory modelHistory = (ModelHistory) i$.next();
                representations.add(new ModelRepresentation(modelHistory));
            }

            result.setData(representations);
        }

        result.setSize(representations.size());
        result.setTotal((long) representations.size());
        result.setStart(0);
        return result;
    }

    public ModelRepresentation getProcessModelHistory(String modelId, String modelHistoryId) {
        ModelHistory modelHistory = this.modelService.getModelHistory(modelId, modelHistoryId);
        return new ModelRepresentation(modelHistory);
    }
}