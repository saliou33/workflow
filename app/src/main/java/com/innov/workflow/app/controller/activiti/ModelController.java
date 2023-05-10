package com.innov.workflow.app.controller.activiti;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.innov.workflow.app.dto.PaginationDTO;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.domain.activiti.ModelParam;
import com.innov.workflow.core.domain.page.TableDataInfo;
import com.innov.workflow.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.poi.util.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/activiti/model")
@AllArgsConstructor
public class ModelController  {

    RepositoryService repositoryService;

    private ObjectMapper objectMapper;

    @RequestMapping(value = "/all", method = RequestMethod.POST)
    @ResponseBody
    public TableDataInfo modelLists(@RequestParam(required = false) String key, @RequestParam(required = false) String name,
                                    PaginationDTO page) {
        ModelQuery query = repositoryService.createModelQuery();
        if (StringUtils.isNotEmpty(key)) {
            query.modelKey(key);
        }
        if (StringUtils.isNotEmpty(name)) {
            query.modelName(name);
        }

        List<Model> listPage = query.orderByCreateTime().desc().listPage(page.getStart(), page.getPageNumber());

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(listPage);
        rspData.setTotal(query.list().size());
        return rspData;
    }


    @PostMapping("/add")
    public ResponseEntity addSave(ModelParam modelRequest) throws JsonProcessingException {
        Model model = repositoryService.newModel();
        model.setCategory(modelRequest.getCategory());
        model.setKey(modelRequest.getKey());
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, modelRequest.getName());
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, modelRequest.getDescription());
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, modelRequest.getVersion());
        model.setMetaInfo(modelNode.toString());
        model.setName(modelRequest.getName());
        model.setVersion(modelRequest.getVersion());
        ModelQuery modelQuery = repositoryService.createModelQuery();
        List<Model> list = modelQuery.modelKey(modelRequest.getKey()).list();
        if (list.size() > 0) {
            return ApiResponse.error("Model key already exist");
        } else {
            // 保存模型到act_re_model表
            repositoryService.saveModel(model);
            HashMap<String, Object> content = new HashMap();
            content.put("resourceId", model.getId());
            HashMap<String, String> properties = new HashMap();
            properties.put("process_id", modelRequest.getKey());
            properties.put("name", modelRequest.getName());
            properties.put("category", modelRequest.getCategory());
            content.put("properties", properties);
            HashMap<String, String> stencilset = new HashMap();
            stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            content.put("stencilset", stencilset);
            repositoryService.addModelEditorSource(model.getId(), objectMapper.writeValueAsBytes(content));
            return ApiResponse.success(model);
        }
    }

    @RequestMapping("/deploy/{modelId}")
    @ResponseBody
    public ResponseEntity modelDeployment(@PathVariable String modelId) {
        try {
            Model model = repositoryService.getModel(modelId);
            byte[] modelData = repositoryService.getModelEditorSource(modelId);
            JsonNode jsonNode = objectMapper.readTree(modelData);
            BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
            Deployment deploy = repositoryService.createDeployment().category(model.getCategory())
                    .name(model.getName()).key(model.getKey())
                    .addBpmnModel(model.getKey() + ".bpmn20.xml", bpmnModel)
                    .deploy();
            model.setDeploymentId(deploy.getId());
            repositoryService.saveModel(model);
            return ApiResponse.success("model deployed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("model deployment failed");
        }
    }

    @PostMapping("/remove/{modelId}")
    @ResponseBody
    public ResponseEntity removeModel(@PathVariable String modelId) {
        repositoryService.deleteModel(modelId);
        return ApiResponse.success("model deleted");
    }

    @GetMapping("/export/{modelId}")
    public void modelExport(@PathVariable String modelId, HttpServletResponse response) throws IOException {
        byte[] modelData = repositoryService.getModelEditorSource(modelId);
        JsonNode jsonNode = objectMapper.readTree(modelData);
        BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(jsonNode);
        byte[] xmlBytes = (new BpmnXMLConverter()).convertToXML(bpmnModel, "UTF-8");
        ByteArrayInputStream in = new ByteArrayInputStream(xmlBytes);
        IOUtils.copy(in, response.getOutputStream());
        String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
        response.setHeader("Content-Disposition","attachment;filename=" + filename);
        response.setHeader("content-Type", "application/xml");
        response.flushBuffer();
    }


}
