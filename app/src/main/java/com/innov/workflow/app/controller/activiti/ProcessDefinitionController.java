package com.innov.workflow.app.controller.activiti;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.innov.workflow.app.dto.PaginationDTO;
import com.innov.workflow.app.service.ActivitiService;
import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.utils.StringUtils;
import com.innov.workflow.core.domain.activiti.ProcessDefinitionInfo;
import lombok.AllArgsConstructor;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipInputStream;


@RestController
@RequestMapping("/api/activiti/process-definition/")
@AllArgsConstructor
public class ProcessDefinitionController {

    private final RepositoryService repositoryService;

    private final ActivitiService activitiService;

    private final TaskService taskService;

    @PostMapping(value = "/upload")
    @ResponseBody
    public ResponseEntity fileUpload(@RequestParam MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            InputStream is = file.getInputStream();
            if (filename.endsWith("zip")) {
                repositoryService.createDeployment().name(filename).addZipInputStream(new ZipInputStream(is)).deploy();
            } else if (filename.endsWith("bpmn") || filename.endsWith("xml")) {
                repositoryService.createDeployment().name(filename).addInputStream(filename, is).deploy();
            } else {
                return ApiResponse.error("invalid workflow deployment File");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("workflow deployment failed");
        }
        return ApiResponse.success("workflow deployment successful");
    }


    @PostMapping(value = "/all")
    public ResponseEntity getProcessDefinitions(@RequestParam(required = false) String key, @RequestParam(required = false) String name, PaginationDTO page) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();

        if (StringUtils.isNotEmpty(key)) {
            query.processDefinitionKey(key);
        }
        if (StringUtils.isNotEmpty(name)) {
            query.processDefinitionName(name);
        }

        //int total = query.list().size();
        List<ProcessDefinition> pageList = query.listPage(page.getStart(), page.getPageSize());

        List<ProcessDefinitionInfo> mylist = new ArrayList<ProcessDefinitionInfo>();
        for (int i = 0; i < pageList.size(); i++) {
            mylist.add(activitiService.processDefinitionInfoMap(pageList.get(i)));
        }

        return ApiResponse.success(mylist);
    }

    @DeleteMapping(value = "/{deploymentId}")
    public ResponseEntity remove(@PathVariable String deploymentId) {
        try {
            repositoryService.deleteDeployment(deploymentId, true);
        }catch(Exception e) {
            return ApiResponse.error(e.getMessage());
        }
        return ApiResponse.success("deployment deleted");
    }


    @PostMapping(value = "/resource")
    public @ResponseBody byte[] showResource(@RequestBody String deploymentId) throws Exception {

        BpmnModel bpmnModel = repositoryService.getBpmnModel(deploymentId);
        ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();

        List<String> list1 = new ArrayList<>(Arrays.asList("png"));

        InputStream is = diagramGenerator.generateDiagram(bpmnModel, list1);

        return IOUtils.toByteArray(is);
    }

    @GetMapping(value = "/{deploymentId}/{resource}")
    public void showProcessDefinition(@PathVariable String deploymentId, @PathVariable(value = "resource") String resource,
                                      HttpServletResponse response) throws Exception {
        InputStream is = repositoryService.getResourceAsStream(deploymentId, resource);
        ServletOutputStream output = response.getOutputStream();
        IOUtils.copy(is, output);
    }

    @GetMapping(value = "/exchangeProcessToModel/{pdid}")
    public String exchangeProcessToModel(@PathVariable("pdid") String id, HttpServletResponse response) throws Exception {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definition.getId());
        ObjectNode objectNode = new BpmnJsonConverter().convertToJson(bpmnModel);
        Model modelData = repositoryService.newModel();
        modelData.setKey(definition.getKey());
        modelData.setName(definition.getName());
        modelData.setCategory(definition.getCategory());
        ObjectNode modelJson = new ObjectMapper().createObjectNode();
        modelJson.put(ModelDataJsonConstants.MODEL_NAME, definition.getName());
        modelJson.put(ModelDataJsonConstants.MODEL_DESCRIPTION, definition.getDescription());
        List<Model> models = repositoryService.createModelQuery().modelKey(definition.getKey()).list();
        if (models.size() > 0) {
            Integer version = models.get(0).getVersion();
            version++;
            modelJson.put(ModelDataJsonConstants.MODEL_REVISION, version);
            // 删除旧模型
            repositoryService.deleteModel(models.get(0).getId());
            modelData.setVersion(version);
        } else {
            modelJson.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        }
        modelData.setMetaInfo(modelJson.toString());
        modelData.setDeploymentId(definition.getDeploymentId());
        // 保存新模型
        repositoryService.saveModel(modelData);
        // 保存模型json
        repositoryService.addModelEditorSource(modelData.getId(), objectNode.toString().getBytes(StandardCharsets.UTF_8));
        return objectNode.toString();
    }

    @PostMapping("/activate/{processDefinitionId}")
    public  ResponseEntity activateProcess(@PathVariable String processDefinitionId) {
        try {
            repositoryService.activateProcessDefinitionById(processDefinitionId);
        } catch (Exception e) {
            return  ApiResponse.error(e.getMessage());
        }
         return ApiResponse.success("process activated");
    }


    @PostMapping("/suspend/{processDefinitionId}")
    public  ResponseEntity suspendProcess(@PathVariable String processDefinitionId) {
        try {
            repositoryService.suspendProcessDefinitionById(processDefinitionId);
        } catch (Exception e) {
            return  ApiResponse.error(e.getMessage());
        }
        return ApiResponse.success("process suspended");
    }
}
