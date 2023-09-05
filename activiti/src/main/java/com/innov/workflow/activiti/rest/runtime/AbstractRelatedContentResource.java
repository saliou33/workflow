package com.innov.workflow.activiti.rest.runtime;

import com.innov.workflow.activiti.domain.runtime.RelatedContent;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.component.SimpleContentTypeMapper;
import com.innov.workflow.activiti.model.runtime.ProcessInstanceRepresentation;
import com.innov.workflow.activiti.model.runtime.RelatedContentRepresentation;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.InternalServerErrorException;
import com.innov.workflow.activiti.service.exception.NotFoundException;
import com.innov.workflow.activiti.service.exception.NotPermittedException;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import com.innov.workflow.activiti.service.runtime.RelatedContentService;
import com.innov.workflow.activiti.service.runtime.RelatedContentStreamProvider;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public abstract class AbstractRelatedContentResource {
    private static final int MAX_CONTENT_ITEMS = 50;
    @Autowired
    protected PermissionService permissionService;
    @Autowired
    protected RelatedContentService contentService;
    @Autowired
    protected RelatedContentStreamProvider streamProvider;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected SimpleContentTypeMapper simpleTypeMapper;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected RelatedContentStreamProvider relatedContentStreamProvider;
    @Autowired
    protected IdentityService identityService;

    public AbstractRelatedContentResource() {
    }

    public ResultListDataRepresentation getRelatedContentForTask(String taskId) {
        this.permissionService.validateReadPermissionOnTask(identityService.getCurrentUserObject(), taskId);
        return this.createResultRepresentation(this.contentService.getRelatedContentForTask(taskId, 50, 0));
    }

    public ResultListDataRepresentation getRelatedContentForProcessInstance(String processInstanceId) {
        if (!this.permissionService.hasReadPermissionOnProcessInstance(identityService.getCurrentUserObject(), processInstanceId)) {
            throw new NotPermittedException("You are not allowed to read the process with id: " + processInstanceId);
        } else {
            return this.createResultRepresentation(this.contentService.getRelatedContentForProcessInstance(processInstanceId, 50, 0));
        }
    }

    public RelatedContentRepresentation createRelatedContentOnTask(String taskId, MultipartFile file) {
        User user = identityService.getCurrentUserObject();
        Task task = (Task) ((TaskQuery) this.taskService.createTaskQuery().taskId(taskId)).singleResult();
        if (task == null) {
            throw new NotFoundException("Task not found or already completed: " + taskId);
        } else if (!this.permissionService.canAddRelatedContentToTask(user, taskId)) {
            throw new NotPermittedException("You are not allowed to read the task with id: " + taskId);
        } else {
            return this.uploadFile(user, file, taskId, task.getProcessInstanceId());
        }
    }

    public RelatedContentRepresentation createRelatedContentOnTask(String taskId, RelatedContentRepresentation relatedContent) {
        User user = identityService.getCurrentUserObject();
        Task task = (Task) ((TaskQuery) this.taskService.createTaskQuery().taskId(taskId)).singleResult();
        if (task == null) {
            throw new NotFoundException("Task not found or already completed: " + taskId);
        } else if (!this.permissionService.canAddRelatedContentToTask(user, taskId)) {
            throw new NotPermittedException("You are not allowed to read the task with id: " + taskId);
        } else {
            return this.addRelatedContent(relatedContent, taskId, task.getProcessInstanceId(), true);
        }
    }

    public RelatedContentRepresentation createRelatedContentOnProcessInstance(String processInstanceId, RelatedContentRepresentation relatedContent) {
        User user = identityService.getCurrentUserObject();
        if (!this.permissionService.canAddRelatedContentToProcessInstance(user, processInstanceId)) {
            throw new NotPermittedException("You are not allowed to read the process with id: " + processInstanceId);
        } else {
            return this.addRelatedContent(relatedContent, (String) null, processInstanceId, true);
        }
    }

    public RelatedContentRepresentation createRelatedContentOnProcessInstance(String processInstanceId, MultipartFile file) {
        User user = identityService.getCurrentUserObject();
        if (!this.permissionService.canAddRelatedContentToProcessInstance(user, processInstanceId)) {
            throw new NotPermittedException("You are not allowed to read the process with id: " + processInstanceId);
        } else {
            return this.uploadFile(user, file, (String) null, processInstanceId);
        }
    }

    public RelatedContentRepresentation createTemporaryRawRelatedContent(MultipartFile file) {
        User user = identityService.getCurrentUserObject();
        return this.uploadFile(user, file, (String) null, (String) null);
    }

    public RelatedContentRepresentation createTemporaryRelatedContent(RelatedContentRepresentation relatedContent) {
        return this.addRelatedContent(relatedContent, (String) null, (String) null, false);
    }

    public void deleteContent(Long contentId, HttpServletResponse response) {
        RelatedContent content = this.contentService.getRelatedContent(contentId, false);
        if (content == null) {
            throw new NotFoundException("No content found with id: " + contentId);
        } else if (!this.permissionService.hasWritePermissionOnRelatedContent(identityService.getCurrentUserObject(), content)) {
            throw new NotPermittedException("You are not allowed to delete the content with id: " + contentId);
        } else if (content.getField() != null) {
            throw new NotPermittedException("You are not allowed to delete the content with id: " + contentId);
        } else {
            this.contentService.deleteRelatedContent(content);
        }
    }

    public RelatedContentRepresentation getContent(Long contentId) {
        RelatedContent content = this.contentService.getRelatedContent(contentId, false);
        if (content == null) {
            throw new NotFoundException("No content found with id: " + contentId);
        } else if (!this.permissionService.canDownloadContent(identityService.getCurrentUserObject(), content)) {
            throw new NotPermittedException("You are not allowed to view the content with id: " + contentId);
        } else {
            return this.createRelatedContentResponse(content);
        }
    }

    public void getRawContent(Long contentId, HttpServletResponse response) {
        RelatedContent content = this.contentService.getRelatedContent(contentId, false);
        if (content == null) {
            throw new NotFoundException("No content found with id: " + contentId);
        } else if (!content.isContentAvailable() || content.getContentStoreId() == null && !content.isLink()) {
            throw new NotFoundException("Raw content not yet available for id: " + contentId);
        } else if (!this.permissionService.canDownloadContent(identityService.getCurrentUserObject(), content)) {
            throw new NotPermittedException("You are not allowed to read the content with id: " + contentId);
        } else {
            if (content.getMimeType() != null) {
                response.setContentType(content.getMimeType());
                response.setHeader("Content-Disposition", "attachment; filename=\""+content.getName()+"\"");
            }
            InputStream inputstream = null;
            try {
                inputstream = this.streamProvider.getContentStream(content);
                IOUtils.copy(inputstream, response.getOutputStream());
            } catch (IOException var9) {
                throw new InternalServerErrorException("Error while writing raw content data for content: " + contentId, var9);
            } finally {
                if (inputstream != null) {
                    IOUtils.closeQuietly(inputstream);
                }

            }

        }
    }

    public ResultListDataRepresentation getRelatedProcessInstancesForContent(String source, String sourceId) {
        Page<RelatedContent> relatedContents = this.contentService.getRelatedContent(source, sourceId, 50, 0);
        Set<String> processInstanceIds = new HashSet(relatedContents.getSize());
        Iterator i$ = relatedContents.iterator();

        while (i$.hasNext()) {
            RelatedContent relatedContent = (RelatedContent) i$.next();
            processInstanceIds.add(relatedContent.getProcessInstanceId());
        }

        Object processInstances;
        if (processInstanceIds.isEmpty()) {
            processInstances = new LinkedList();
        } else {
            HistoricProcessInstanceQuery processInstanceQuery = this.historyService.createHistoricProcessInstanceQuery();
            User currentUser = identityService.getCurrentUserObject();
            processInstanceQuery.involvedUser(String.valueOf(currentUser.getId()));
            processInstanceQuery.processInstanceIds(processInstanceIds);
            processInstanceQuery.orderByProcessInstanceId().desc();
            processInstances = processInstanceQuery.listPage(0, 50);
        }

        ResultListDataRepresentation result = new ResultListDataRepresentation(this.convertInstanceList((List) processInstances));
        return result;
    }

    protected List<ProcessInstanceRepresentation> convertInstanceList(List<HistoricProcessInstance> instances) {
        List<ProcessInstanceRepresentation> result = new ArrayList();
        if (CollectionUtils.isNotEmpty(instances)) {
            Iterator i$ = instances.iterator();

            while (i$.hasNext()) {
                HistoricProcessInstance processInstance = (HistoricProcessInstance) i$.next();
                User userRep = null;
                if (processInstance.getStartUserId() != null) {
                    userRep = this.identityService.getUser(processInstance.getStartUserId());

                }

                ProcessDefinitionEntity procDef = (ProcessDefinitionEntity) this.repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
                ProcessInstanceRepresentation instanceRepresentation = new ProcessInstanceRepresentation(processInstance, procDef, procDef.isGraphicalNotationDefined(), userRep);
                result.add(instanceRepresentation);
            }
        }

        return result;
    }

    protected RelatedContentRepresentation uploadFile(User user, MultipartFile file, String taskId, String processInstanceId) {
        if (file != null && file.getName() != null) {
            try {
                String contentType = file.getContentType();
                if (StringUtils.equals(file.getContentType(), "application/octet-stream")) {
                    contentType = this.getContentTypeForFileExtension(file);
                }

                RelatedContent relatedContent = this.contentService.createRelatedContent(user, this.getFileName(file), (String) null, (String) null, taskId, processInstanceId, contentType, file.getInputStream(), file.getSize(), true, false);
                return new RelatedContentRepresentation(relatedContent, this.simpleTypeMapper);
            } catch (IOException var7) {
                throw new BadRequestException("Error while reading file data", var7);
            }
        } else {
            throw new BadRequestException("File to upload is missing");
        }
    }

    protected RelatedContentRepresentation addRelatedContent(RelatedContentRepresentation relatedContent, String taskId, String processInstanceId, boolean isRelatedContent) {
        if (relatedContent.getSource() != null && relatedContent.getSourceId() != null && relatedContent.getName() != null) {
            RelatedContent result = this.contentService.createRelatedContent(identityService.getCurrentUserObject(), relatedContent.getName(), relatedContent.getSource(), relatedContent.getSourceId(), taskId, processInstanceId, relatedContent.getMimeType(), (InputStream) null, (Long) null, isRelatedContent, relatedContent.isLink());
            return new RelatedContentRepresentation(result, this.simpleTypeMapper);
        } else {
            throw new BadRequestException("Name, source and sourceId are required paremeters");
        }
    }

    protected String getFileName(MultipartFile file) {
        return file.getOriginalFilename() != null ? file.getOriginalFilename() : "Nameless file";
    }

    protected ResultListDataRepresentation createResultRepresentation(Page<RelatedContent> results) {
        List<RelatedContentRepresentation> resultList = new ArrayList(results.getNumberOfElements());
        Iterator i$ = results.iterator();

        while (i$.hasNext()) {
            RelatedContent content = (RelatedContent) i$.next();
            resultList.add(this.createRelatedContentResponse(content));
        }

        ResultListDataRepresentation result = new ResultListDataRepresentation(resultList);
        result.setTotal(results.getTotalElements());
        return result;
    }

    protected RelatedContentRepresentation createRelatedContentResponse(RelatedContent relatedContent) {
        RelatedContentRepresentation relatedContentResponse = new RelatedContentRepresentation(relatedContent, this.simpleTypeMapper);
        return relatedContentResponse;
    }

    protected String getContentTypeForFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String contentType = null;
        if (!fileName.endsWith(".jpeg") && !fileName.endsWith(".jpg")) {
            if (fileName.endsWith("gif")) {
                contentType = "image/gif";
            } else if (fileName.endsWith("png")) {
                contentType = "image/png";
            } else if (fileName.endsWith("bmp")) {
                contentType = "image/bmp";
            } else if (!fileName.endsWith("tif") && !fileName.endsWith(".tiff")) {
                if (fileName.endsWith("png")) {
                    contentType = "image/png";
                } else if (fileName.endsWith("doc")) {
                    contentType = "application/msword";
                } else if (fileName.endsWith("docx")) {
                    contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                } else if (fileName.endsWith("docm")) {
                    contentType = "application/vnd.ms-word.document.macroenabled.12";
                } else if (fileName.endsWith("dotm")) {
                    contentType = "application/vnd.ms-word.template.macroenabled.12";
                } else if (fileName.endsWith("odt")) {
                    contentType = "application/vnd.oasis.opendocument.text";
                } else if (fileName.endsWith("ott")) {
                    contentType = "application/vnd.oasis.opendocument.text-template";
                } else if (fileName.endsWith("rtf")) {
                    contentType = "application/rtf";
                } else if (fileName.endsWith("txt")) {
                    contentType = "application/text";
                } else if (fileName.endsWith("xls")) {
                    contentType = "application/vnd.ms-excel";
                } else if (fileName.endsWith("xlsx")) {
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                } else if (fileName.endsWith("xlsb")) {
                    contentType = "application/vnd.ms-excel.sheet.binary.macroenabled.12";
                } else if (fileName.endsWith("xltx")) {
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.template";
                } else if (fileName.endsWith("ods")) {
                    contentType = "application/vnd.oasis.opendocument.spreadsheet";
                } else if (fileName.endsWith("ppt")) {
                    contentType = "application/vnd.ms-powerpoint";
                } else if (fileName.endsWith("pptx")) {
                    contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
                } else if (fileName.endsWith("ppsm")) {
                    contentType = "application/vnd.ms-powerpoint.slideshow.macroenabled.12";
                } else if (fileName.endsWith("ppsx")) {
                    contentType = "application/vnd.openxmlformats-officedocument.presentationml.slideshow";
                } else {
                    if (!fileName.endsWith("odp")) {
                        return file.getContentType();
                    }

                    contentType = "application/vnd.oasis.opendocument.presentation";
                }
            } else {
                contentType = "image/tiff";
            }
        } else {
            contentType = "image/jpeg";
        }

        return contentType;
    }
}
