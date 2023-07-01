package com.innov.workflow.activiti.rest.runtime;

import com.innov.workflow.activiti.model.idm.UserRepresentation;
import com.innov.workflow.activiti.model.runtime.TaskRepresentation;
import com.innov.workflow.activiti.model.runtime.TaskUpdateRepresentation;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.service.exception.NotFoundException;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import com.innov.workflow.activiti.util.TaskUtil;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public abstract class AbstractTaskResource {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTaskResource.class);
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected PermissionService permissionService;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected IdentityService identityService;

    public AbstractTaskResource() {
    }

    public TaskRepresentation getTask(String taskId, HttpServletResponse response) {
        User currentUser = identityService.getCurrentUserObject();
        HistoricTaskInstance task = this.permissionService.validateReadPermissionOnTask(currentUser, taskId);
        ProcessDefinition processDefinition = null;
        if (StringUtils.isNotEmpty(task.getProcessDefinitionId())) {
            try {
                processDefinition = this.repositoryService.getProcessDefinition(task.getProcessDefinitionId());
            } catch (ActivitiException var7) {
                logger.error("Error getting process definition " + task.getProcessDefinitionId(), var7);
            }
        }

        TaskRepresentation rep = new TaskRepresentation(task, processDefinition);
        TaskUtil.fillPermissionInformation(rep, task, currentUser, this.identityService, this.historyService, this.repositoryService);
        this.populateAssignee(task, rep);
        rep.setInvolvedPeople(this.getInvolvedUsers(taskId));
        return rep;
    }

    protected void populateAssignee(TaskInfo task, TaskRepresentation rep) {
        if (task.getAssignee() != null) {
            User user = this.identityService.getUser(task.getAssignee());
            if (user != null) {
                rep.setAssignee(new UserRepresentation(user));
            }
        }

    }

    protected List<UserRepresentation> getInvolvedUsers(String taskId) {
        List<HistoricIdentityLink> idLinks = this.historyService.getHistoricIdentityLinksForTask(taskId);
        List<UserRepresentation> result = new ArrayList(idLinks.size());
        Iterator i$ = idLinks.iterator();

        while (i$.hasNext()) {
            HistoricIdentityLink link = (HistoricIdentityLink) i$.next();
            if (link.getUserId() != null && !"assignee".equals(link.getType())) {


                User user = this.identityService.getUser(link.getUserId());
                if (user != null) {
                    result.add(new UserRepresentation(user));
                }

            }
        }

        return result;
    }

    public TaskRepresentation updateTask(String taskId, TaskUpdateRepresentation updated) {
        Task task = (Task) ((TaskQuery) this.taskService.createTaskQuery().taskId(taskId)).singleResult();
        if (task == null) {
            throw new NotFoundException("Task with id: " + taskId + " does not exist");
        } else {
            this.permissionService.validateReadPermissionOnTask(identityService.getCurrentUserObject(), task.getId());
            if (updated.isNameSet()) {
                task.setName(updated.getName());
            }

            if (updated.isDescriptionSet()) {
                task.setDescription(updated.getDescription());
            }

            if (updated.isDueDateSet()) {
                task.setDueDate(updated.getDueDate());
            }

            this.taskService.saveTask(task);
            return new TaskRepresentation(task);
        }
    }
}
