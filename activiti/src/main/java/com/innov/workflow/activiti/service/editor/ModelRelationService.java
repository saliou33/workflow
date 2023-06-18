package com.innov.workflow.activiti.service.editor;

import com.innov.workflow.activiti.domain.editor.ModelInformation;
import com.innov.workflow.activiti.repository.editor.ModelRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelRelationService {

    private final ModelRelationRepository modelRelationRepository;

    public List<ModelInformation> findReferencedModels(String modelId) {
        return this.modelRelationRepository.findModelInformationByParentModelId(modelId);
    }

    public List<ModelInformation> findParentModels(String modelId) {
        return this.modelRelationRepository.findModelInformationByChildModelId(modelId);
    }
}
