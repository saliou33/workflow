package com.innov.workflow.activiti.rest.runtime;

import com.innov.workflow.activiti.domain.runtime.Comment;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.runtime.CommentRepresentation;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.NotFoundException;
import com.innov.workflow.activiti.service.exception.NotPermittedException;
import com.innov.workflow.activiti.service.runtime.CommentService;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public abstract class AbstractCommentsResource {
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private IdentityService identityService;


    public AbstractCommentsResource() {
    }

    public ResultListDataRepresentation getTaskComments(String taskId, Boolean latestFirst) {
        User currentUser = identityService.getCurrentUserObject();
        this.checkReadPermissionOnTask(currentUser, taskId);
        List<Comment> comments = this.commentService.getCommentsForTask(taskId, Boolean.TRUE.equals(latestFirst));
        List<CommentRepresentation> commentList = new ArrayList();
        Iterator i$ = comments.iterator();

        while (i$.hasNext()) {
            Comment comment = (Comment) i$.next();
            commentList.add(new CommentRepresentation(comment));
        }

        return new ResultListDataRepresentation(commentList);
    }

    public CommentRepresentation addTaskComment(CommentRepresentation commentRequest, String taskId) {
        if (StringUtils.isBlank(commentRequest.getMessage())) {
            throw new BadRequestException("Comment should not be empty");
        } else {
            HistoricTaskInstance task = (HistoricTaskInstance) ((HistoricTaskInstanceQuery) this.historyService.createHistoricTaskInstanceQuery().taskId(taskId)).singleResult();
            if (task == null) {
                throw new NotFoundException("No task found with id: " + taskId);
            } else {
                User currentUser = identityService.getCurrentUserObject();
                this.checkReadPermissionOnTask(currentUser, taskId);
                Comment comment = this.commentService.createComment(commentRequest.getMessage(), currentUser, task.getId(), task.getProcessInstanceId());
                return new CommentRepresentation(comment);
            }
        }
    }

    public ResultListDataRepresentation getProcessInstanceComments(String processInstanceId, Boolean latestFirst) {
        User currentUser = identityService.getCurrentUserObject();
        this.checkReadPermissionOnProcessInstance(currentUser, processInstanceId);
        List<Comment> comments = this.commentService.getCommentsForProcessInstance(processInstanceId, Boolean.TRUE.equals(latestFirst));
        List<CommentRepresentation> commentList = new ArrayList();
        Iterator i$ = comments.iterator();

        while (i$.hasNext()) {
            Comment comment = (Comment) i$.next();
            commentList.add(new CommentRepresentation(comment));
        }

        return new ResultListDataRepresentation(commentList);
    }

    public CommentRepresentation addProcessInstanceComment(CommentRepresentation commentRequest, String processInstanceId) {
        if (StringUtils.isBlank(commentRequest.getMessage())) {
            throw new BadRequestException("Comment should not be empty");
        } else {
            HistoricProcessInstance processInstance = (HistoricProcessInstance) this.historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (processInstance == null) {
                throw new NotFoundException("No process instance found with id: " + processInstanceId);
            } else {
                User currentUser = identityService.getCurrentUserObject();
                this.checkReadPermissionOnProcessInstance(currentUser, processInstanceId);
                Comment comment = this.commentService.createComment(commentRequest.getMessage(), currentUser, processInstanceId);
                return new CommentRepresentation(comment);
            }
        }
    }

    protected void checkReadPermissionOnTask(User user, String taskId) {
        if (taskId == null) {
            throw new BadRequestException("Task id is required");
        } else {
            this.permissionService.validateReadPermissionOnTask(identityService.getCurrentUserObject(), taskId);
        }
    }

    protected void checkReadPermissionOnProcessInstance(User user, String processInstanceId) {
        if (processInstanceId == null) {
            throw new BadRequestException("Process instance id is required");
        } else if (!this.permissionService.hasReadPermissionOnProcessInstance(identityService.getCurrentUserObject(), processInstanceId)) {
            throw new NotPermittedException("You are not permitted to read process instance with id: " + processInstanceId);
        }
    }
}
