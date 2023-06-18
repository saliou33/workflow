package com.innov.workflow.activiti.service.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.domain.editor.*;
import com.innov.workflow.activiti.repository.editor.ModelHistoryRepository;
import com.innov.workflow.activiti.repository.editor.ModelRepository;
import com.innov.workflow.activiti.service.api.AppDefinitionService;
import com.innov.workflow.activiti.service.api.AppDefinitionServiceRepresentation;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.idm.config.jwt.JwtUtils;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class AppDefinitionServiceImpl implements AppDefinitionService {
    private final Logger logger = LoggerFactory.getLogger(AppDefinitionServiceImpl.class);
    @Autowired
    protected ModelRepository modelRepository;
    @Autowired
    protected ModelHistoryRepository modelHistoryRepository;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected JwtUtils jwtUtils;

    public AppDefinitionServiceImpl() {
    }

    public List<AppDefinitionServiceRepresentation> getAppDefinitions() {
        Map<String, AbstractModel> modelMap = new HashMap();
        List<AppDefinitionServiceRepresentation> resultList = new ArrayList();


        List<Model> createdByModels = this.modelRepository.findModelsCreatedBy(jwtUtils.getCurrentUser().getId(), 3, Sort.by(Direction.ASC, "name"));
        Iterator i$ = createdByModels.iterator();

        while (i$.hasNext()) {
            Model model = (Model) i$.next();
            modelMap.put(model.getId(), model);
        }

        i$ = modelMap.values().iterator();

        while (i$.hasNext()) {
            AbstractModel model = (AbstractModel) i$.next();
            resultList.add(this.createAppDefinition(model));
        }

        return resultList;
    }

    public List<AppDefinitionServiceRepresentation> getDeployableAppDefinitions(User user) {
        Map<String, ModelHistory> modelMap = new HashMap();
        List<AppDefinitionServiceRepresentation> resultList = new ArrayList();
        List<ModelHistory> createdByModels = this.modelHistoryRepository.findByCreatedByAndModelTypeAndRemovalDateIsNull(user.getId(), 3);
        Iterator i$ = createdByModels.iterator();

        ModelHistory model;
        while (i$.hasNext()) {
            model = (ModelHistory) i$.next();
            if (modelMap.containsKey(model.getModelId())) {
                if (model.getVersion() > ((ModelHistory) modelMap.get(model.getModelId())).getVersion()) {
                    modelMap.put(model.getModelId(), model);
                }
            } else {
                modelMap.put(model.getModelId(), model);
            }
        }

        i$ = modelMap.values().iterator();

        while (i$.hasNext()) {
            model = (ModelHistory) i$.next();
            Model latestModel = (Model) modelRepository.findById(model.getModelId()).orElse(null);
            if (latestModel != null) {
                resultList.add(this.createAppDefinition(model));
            }
        }

        return resultList;
    }

    protected AppDefinitionServiceRepresentation createAppDefinition(AbstractModel model) {
        AppDefinitionServiceRepresentation resultInfo = new AppDefinitionServiceRepresentation();
        if (model instanceof ModelHistory) {
            resultInfo.setId(((ModelHistory) model).getModelId());
        } else {
            resultInfo.setId(model.getId());
        }

        resultInfo.setName(model.getName());
        resultInfo.setDescription(model.getDescription());
        resultInfo.setVersion(model.getVersion());
        resultInfo.setDefinition(model.getModelEditorJson());
        AppDefinition appDefinition = null;

        try {
            appDefinition = (AppDefinition) this.objectMapper.readValue(model.getModelEditorJson(), AppDefinition.class);
        } catch (Exception var8) {
            this.logger.error("Error deserializing app " + model.getId(), var8);
            throw new InternalServerErrorException("Could not deserialize app definition");
        }

        if (appDefinition != null) {
            resultInfo.setTheme(appDefinition.getTheme());
            resultInfo.setIcon(appDefinition.getIcon());
            List<AppModelDefinition> models = appDefinition.getModels();
            if (CollectionUtils.isNotEmpty(models)) {
                List<String> modelIds = new ArrayList();
                Iterator i$ = models.iterator();

                while (i$.hasNext()) {
                    AppModelDefinition appModelDef = (AppModelDefinition) i$.next();
                    modelIds.add(appModelDef.getId());
                }

                resultInfo.setModels(modelIds);
            }
        }

        return resultInfo;
    }

    public String getDefinitionIdForModelAndUser(String modelId, User user) {
        String appDefinitionId = this.modelRepository.appDefinitionIdByModelAndUser(modelId, user.getId());
        return appDefinitionId;
    }
}