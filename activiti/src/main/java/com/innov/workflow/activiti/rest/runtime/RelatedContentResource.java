package com.innov.workflow.activiti.rest.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.runtime.RelatedContentRepresentation;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class RelatedContentResource extends AbstractRelatedContentResource {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRelatedContentResource.class);
    protected ObjectMapper objectMapper = new ObjectMapper();

    public RelatedContentResource() {
    }

    @RequestMapping(
            value = {"/activiti/tasks/{taskId}/content"},
            method = {RequestMethod.GET}
    )
    public ResultListDataRepresentation getRelatedContentForTask(@PathVariable("taskId") String taskId) {
        return super.getRelatedContentForTask(taskId);
    }

    @RequestMapping(
            value = {"/activiti/process-instances/{processInstanceId}/content"},
            method = {RequestMethod.GET}
    )
    public ResultListDataRepresentation getRelatedContentForProcessInstance(@PathVariable("processInstanceId") String processInstanceId) {
        return super.getRelatedContentForProcessInstance(processInstanceId);
    }

    @RequestMapping(
            value = {"/activiti/content/{source}/{sourceId}/process-instances"},
            method = {RequestMethod.GET}
    )
    public ResultListDataRepresentation getRelatedProcessInstancesForContent(@PathVariable("source") String source, @PathVariable("sourceId") String sourceId) {
        return super.getRelatedProcessInstancesForContent(source, sourceId);
    }

    @RequestMapping(
            value = {"/activiti/tasks/{taskId}/raw-content"},
            method = {RequestMethod.POST}
    )
    public RelatedContentRepresentation createRelatedContentOnTask(@PathVariable("taskId") String taskId, @RequestParam("file") MultipartFile file) {
        return super.createRelatedContentOnTask(taskId, file);
    }

    @RequestMapping(
            value = {"/activiti/tasks/{taskId}/raw-content/text"},
            method = {RequestMethod.POST}
    )
    public String createRelatedContentOnTaskText(@PathVariable("taskId") String taskId, @RequestParam("file") MultipartFile file) {
        RelatedContentRepresentation relatedContentRepresentation = super.createRelatedContentOnTask(taskId, file);
        String relatedContentJson = null;

        try {
            relatedContentJson = this.objectMapper.writeValueAsString(relatedContentRepresentation);
            return relatedContentJson;
        } catch (Exception var6) {
            logger.error("Error while processing RelatedContent representation json", var6);
            throw new InternalServerErrorException("Related Content on task could not be saved");
        }
    }

    @RequestMapping(
            value = {"/activiti/tasks/{taskId}/content"},
            method = {RequestMethod.POST}
    )
    public RelatedContentRepresentation createRelatedContentOnTask(@PathVariable("taskId") String taskId, @RequestBody RelatedContentRepresentation relatedContent) {
        return super.createRelatedContentOnTask(taskId, relatedContent);
    }

    @RequestMapping(
            value = {"/activiti/processes/{processInstanceId}/content"},
            method = {RequestMethod.POST}
    )
    public RelatedContentRepresentation createRelatedContentOnProcessInstance(@PathVariable("processInstanceId") String processInstanceId, @RequestBody RelatedContentRepresentation relatedContent) {
        return super.createRelatedContentOnProcessInstance(processInstanceId, relatedContent);
    }

    @RequestMapping(
            value = {"/activiti/process-instances/{processInstanceId}/raw-content"},
            method = {RequestMethod.POST}
    )
    public RelatedContentRepresentation createRelatedContentOnProcessInstance(@PathVariable("processInstanceId") String processInstanceId, @RequestParam("file") MultipartFile file) {
        return super.createRelatedContentOnProcessInstance(processInstanceId, file);
    }

    @RequestMapping(
            value = {"/activiti/process-instances/{processInstanceId}/raw-content/text"},
            method = {RequestMethod.POST}
    )
    public String createRelatedContentOnProcessInstanceText(@PathVariable("processInstanceId") String processInstanceId, @RequestParam("file") MultipartFile file) {
        RelatedContentRepresentation relatedContentRepresentation = super.createRelatedContentOnProcessInstance(processInstanceId, file);
        String relatedContentJson = null;

        try {
            relatedContentJson = this.objectMapper.writeValueAsString(relatedContentRepresentation);
            return relatedContentJson;
        } catch (Exception var6) {
            logger.error("Error while processing RelatedContent representation json", var6);
            throw new InternalServerErrorException("Related Content on process instance could not be saved");
        }
    }

    @RequestMapping(
            value = {"/activiti/content/raw"},
            method = {RequestMethod.POST}
    )
    public RelatedContentRepresentation createTemporaryRawRelatedContent(@RequestParam("file") MultipartFile file) {
        return super.createTemporaryRawRelatedContent(file);
    }

    @RequestMapping(
            value = {"/activiti/content/raw/text"},
            method = {RequestMethod.POST}
    )
    public String createTemporaryRawRelatedContentText(@RequestParam("file") MultipartFile file) {
        RelatedContentRepresentation relatedContentRepresentation = super.createTemporaryRawRelatedContent(file);
        String relatedContentJson = null;

        try {
            relatedContentJson = this.objectMapper.writeValueAsString(relatedContentRepresentation);
            return relatedContentJson;
        } catch (Exception var5) {
            logger.error("Error while processing RelatedContent representation json", var5);
            throw new InternalServerErrorException("Related Content could not be saved");
        }
    }

    @RequestMapping(
            value = {"/activiti/content"},
            method = {RequestMethod.POST}
    )
    public RelatedContentRepresentation createTemporaryRelatedContent(@RequestBody RelatedContentRepresentation relatedContent) {
        return this.addRelatedContent(relatedContent, (String) null, (String) null, false);
    }

    @RequestMapping(
            value = {"/activiti/content/{contentId}"},
            method = {RequestMethod.DELETE}
    )
    public void deleteContent(@PathVariable("contentId") Long contentId, HttpServletResponse response) {
        super.deleteContent(contentId, response);
    }

    @RequestMapping(
            value = {"/activiti/content/{contentId}"},
            method = {RequestMethod.GET}
    )
    public RelatedContentRepresentation getContent(@PathVariable("contentId") Long contentId) {
        return super.getContent(contentId);
    }

    @RequestMapping(
            value = {"/activiti/content/{contentId}/raw"},
            method = {RequestMethod.GET}
    )
    public void getRawContent(@PathVariable("contentId") Long contentId, HttpServletResponse response) {
        super.getRawContent(contentId, response);
    }
}
