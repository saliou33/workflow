package com.innov.workflow.activiti.rest.editor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.domain.editor.AppDefinition;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.editor.AppDefinitionListModelRepresentation;
import com.innov.workflow.activiti.model.editor.ModelRepresentation;
import com.innov.workflow.activiti.old.service.IdentityService;
import com.innov.workflow.activiti.repository.editor.ModelRepository;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.activiti.util.XmlUtil;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@RestController
public class AbstractModelsResource {
    protected static final String FILTER_SHARED_WITH_ME = "sharedWithMe";
    protected static final String FILTER_SHARED_WITH_OTHERS = "sharedWithOthers";
    protected static final String FILTER_FAVORITE = "favorite";
    protected static final String SORT_NAME_ASC = "nameAsc";
    protected static final String SORT_NAME_DESC = "nameDesc";
    protected static final String SORT_MODIFIED_ASC = "modifiedAsc";
    protected static final int MIN_FILTER_LENGTH = 1;
    private final Logger logger = LoggerFactory.getLogger(AbstractModelsResource.class);
    @Autowired
    protected ModelRepository modelRepository;
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected IdentityService identityService;
    @Autowired
    protected ObjectMapper objectMapper;
    protected BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
    protected BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();

    public AbstractModelsResource() {
    }

    public ResultListDataRepresentation getModels(String filter, String sort, Integer modelType, HttpServletRequest request) {
        String filterText = null;
        List<NameValuePair> params = URLEncodedUtils.parse(request.getQueryString(), Charset.forName("UTF-8"));
        NameValuePair nameValuePair;
        if (params != null) {
            Iterator i$ = params.iterator();

            while (i$.hasNext()) {
                nameValuePair = (NameValuePair) i$.next();
                if ("filterText".equalsIgnoreCase(nameValuePair.getName())) {
                    filterText = nameValuePair.getValue();
                }
            }
        }

        List<ModelRepresentation> resultList = new ArrayList();
        nameValuePair = null;
        String validFilter = this.makeValidFilterText(filterText);
        User user = identityService.getCurrentUserObject();
        List models;
        if (validFilter != null) {
            models = this.modelRepository.findModelsCreatedBy(user.getId(), modelType, validFilter, this.getSort(sort, false));
        } else {
            models = this.modelRepository.findModelsCreatedBy(user.getId(), modelType, this.getSort(sort, false));
        }

        if (CollectionUtils.isNotEmpty(models)) {
            List<String> addedModelIds = new ArrayList();
            Iterator i$ = models.iterator();

            while (i$.hasNext()) {
                Model model = (Model) i$.next();
                if (!addedModelIds.contains(model.getId())) {
                    addedModelIds.add(model.getId());
                    ModelRepresentation representation = this.createModelRepresentation(model);
                    resultList.add(representation);
                }
            }
        }

        ResultListDataRepresentation result = new ResultListDataRepresentation(resultList);
        return result;
    }

    public ResultListDataRepresentation getModelsToIncludeInAppDefinition() {
        List<ModelRepresentation> resultList = new ArrayList();
        User user = identityService.getCurrentUserObject();
        List<String> addedModelIds = new ArrayList();
        List<Model> models = this.modelRepository.findModelsCreatedBy(user.getId(), 0, this.getSort((String) null, false));
        if (CollectionUtils.isNotEmpty(models)) {
            Iterator i$ = models.iterator();

            while (i$.hasNext()) {
                Model model = (Model) i$.next();
                if (!addedModelIds.contains(model.getId())) {
                    addedModelIds.add(model.getId());
                    ModelRepresentation representation = this.createModelRepresentation(model);
                    resultList.add(representation);
                }
            }
        }

        ResultListDataRepresentation result = new ResultListDataRepresentation(resultList);
        return result;
    }

    public ModelRepresentation importProcessModel(HttpServletRequest request, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".bpmn") && !fileName.endsWith(".bpmn20.xml")) {
            throw new BadRequestException("Invalid file name, only .bpmn and .bpmn20.xml files are supported not " + fileName);
        } else {
            try {
                XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
                InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), "UTF-8");
                XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
                BpmnModel bpmnModel = this.bpmnXmlConverter.convertToBpmnModel(xtr);
                if (CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
                    throw new BadRequestException("No process found in definition " + fileName);
                } else {
                    if (bpmnModel.getLocationMap().size() == 0) {
                        BpmnAutoLayout bpmnLayout = new BpmnAutoLayout(bpmnModel);
                        bpmnLayout.execute();
                    }

                    ObjectNode modelNode = this.bpmnJsonConverter.convertToJson(bpmnModel);
                    Process process = bpmnModel.getMainProcess();
                    String name = process.getId();
                    if (StringUtils.isNotEmpty(process.getName())) {
                        name = process.getName();
                    }

                    String description = process.getDocumentation();
                    ModelRepresentation model = new ModelRepresentation();
                    model.setKey(process.getId());
                    model.setName(name);
                    model.setDescription(description);
                    model.setModelType(0);
                    Model newModel = this.modelService.createModel(model, modelNode.toString(), identityService.getCurrentUserObject());
                    return new ModelRepresentation(newModel);
                }
            } catch (BadRequestException var14) {
                throw var14;
            } catch (Exception var15) {
                this.logger.error("Import failed for " + fileName, var15);
                throw new BadRequestException("Import failed for " + fileName + ", error message " + var15.getMessage());
            }
        }
    }

    protected ModelRepresentation createModelRepresentation(AbstractModel model) {
        ModelRepresentation representation = null;
        if (model.getModelType() != null && model.getModelType() == 3) {
            representation = new AppDefinitionListModelRepresentation(model);
            AppDefinition appDefinition = null;

            try {
                appDefinition = (AppDefinition) this.objectMapper.readValue(model.getModelEditorJson(), AppDefinition.class);
            } catch (Exception var5) {
                this.logger.error("Error deserializing app " + model.getId(), var5);
                throw new InternalServerErrorException("Could not deserialize app definition");
            }

            ((AppDefinitionListModelRepresentation) representation).setAppDefinition(appDefinition);
        } else {
            representation = new ModelRepresentation(model);
        }

        return (ModelRepresentation) representation;
    }

    protected String makeValidFilterText(String filterText) {
        String validFilter = null;
        if (filterText != null) {
            String trimmed = StringUtils.trim(filterText);
            if (trimmed.length() >= 1) {
                validFilter = "%" + trimmed.toLowerCase() + "%";
            }
        }

        return validFilter;
    }

    protected Sort getSort(String sort, boolean prefixWithProcessModel) {
        String propName;
        Sort.Direction direction;
        if ("nameAsc".equals(sort)) {
            if (prefixWithProcessModel) {
                propName = "model.name";
            } else {
                propName = "name";
            }

            direction = Direction.ASC;
        } else if ("nameDesc".equals(sort)) {
            if (prefixWithProcessModel) {
                propName = "model.name";
            } else {
                propName = "name";
            }

            direction = Direction.DESC;
        } else if ("modifiedAsc".equals(sort)) {
            if (prefixWithProcessModel) {
                propName = "model.lastUpdated";
            } else {
                propName = "lastUpdated";
            }

            direction = Direction.ASC;
        } else {
            if (prefixWithProcessModel) {
                propName = "model.lastUpdated";
            } else {
                propName = "lastUpdated";
            }

            direction = Direction.DESC;
        }

        return Sort.by(direction, new String[]{propName});
    }
}
