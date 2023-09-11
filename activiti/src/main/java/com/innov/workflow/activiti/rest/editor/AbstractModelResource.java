package com.innov.workflow.activiti.rest.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.model.editor.ModelRepresentation;
import com.innov.workflow.activiti.service.api.ModelService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.NonJsonResourceNotFoundException;
import com.innov.workflow.activiti.util.XmlUtil;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
public class AbstractModelResource {
    @Autowired
    protected ModelService modelService;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected IdentityService identityService;

    protected BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
    protected BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();

    public AbstractModelResource() {
    }

    public ModelRepresentation getModel(String modelId) {
        Model model = this.modelService.getModel(modelId);
        ModelRepresentation result = new ModelRepresentation(model);
        return result;
    }

    public byte[] getModelThumbnail(String modelId) {
        Model model = this.modelService.getModel(modelId);
        if (model == null) {
            throw new NonJsonResourceNotFoundException();
        } else {
            return model.getThumbnail();
        }
    }

    public ModelRepresentation importNewVersion(String modelId, MultipartFile file) {
        Model processModel = this.modelService.getModel(modelId);
        User currentUser = identityService.getCurrentUserObject();
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".bpmn") && !fileName.endsWith(".bpmn20.xml")) {
            throw new BadRequestException("Invalid file name, only .bpmn and .bpmn20.xml files are supported not " + fileName);
        } else {
            try {
                XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
                InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
                BpmnModel bpmnModel = this.bpmnXMLConverter.convertToBpmnModel(xtr);
                if (CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
                    throw new BadRequestException("No process found in definition " + fileName);
                } else if (bpmnModel.getLocationMap().size() == 0) {
                    throw new BadRequestException("No required BPMN DI information found in definition " + fileName);
                } else {
                    ObjectNode modelNode = this.bpmnJsonConverter.convertToJson(bpmnModel);
                    AbstractModel savedModel = this.modelService.saveModel(modelId, processModel.getName(), processModel.getKey(), processModel.getDescription(), modelNode.toString(), true, "Version import via REST service", currentUser);
                    return new ModelRepresentation(savedModel);
                }
            } catch (BadRequestException var12) {
                throw var12;
            } catch (Exception var13) {
                throw new BadRequestException("Import failed for " + fileName + ", error message " + var13.getMessage());
            }
        }
    }
}
