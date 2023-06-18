package com.innov.workflow.activiti.service.editor;

import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.domain.editor.ModelHistory;
import com.innov.workflow.activiti.repository.editor.ModelHistoryRepository;
import com.innov.workflow.activiti.repository.editor.ModelRepository;
import com.innov.workflow.activiti.service.exception.NotFoundException;
import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BaseActivitiModelService {
    protected static final String PROCESS_NOT_FOUND_MESSAGE_KEY = "PROCESS.ERROR.NOT-FOUND";
    @Autowired
    protected ModelRepository modelRepository;
    @Autowired
    protected ModelHistoryRepository modelHistoryRepository;
    @Autowired
    protected UserService userService;

    public BaseActivitiModelService() {
    }

    protected Model getModel(String modelId, boolean checkRead, boolean checkEdit) {
        Model model = (Model) this.modelRepository.findById(modelId).orElse(null);
        if (model == null) {
            NotFoundException processNotFound = new NotFoundException("No model found with the given id: " + modelId);
            processNotFound.setMessageKey("PROCESS.ERROR.NOT-FOUND");
            throw processNotFound;
        } else {
            return model;
        }
    }

    protected ModelHistory getModelHistory(String modelId, String modelHistoryId, boolean checkRead, boolean checkEdit) {
        Model model = this.getModel(modelId, checkRead, checkEdit);
        ModelHistory modelHistory = (ModelHistory) this.modelHistoryRepository.findById(modelHistoryId).orElse(null);
        if (modelHistory != null && modelHistory.getRemovalDate() == null && modelHistory.getModelId().equals(model.getId())) {
            return modelHistory;
        } else {
            throw new NotFoundException("Model history not found: " + modelHistoryId);
        }
    }

    protected List<String> getGroupIds(Long userId) {

        User user = userService.getUserByUserId(userId);

        List<String> groupIds = new ArrayList();
        for (Group r : user.getGroups()) {
            groupIds.add(r.getId().toString());
        }

        return groupIds;
    }
}