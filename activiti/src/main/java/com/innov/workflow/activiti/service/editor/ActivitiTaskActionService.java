package com.innov.workflow.activiti.service.editor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.model.idm.UserRepresentation;
import com.innov.workflow.activiti.model.runtime.TaskRepresentation;
import com.innov.workflow.activiti.service.exception.BadRequestException;
import com.innov.workflow.activiti.service.exception.NotFoundException;
import com.innov.workflow.activiti.service.exception.NotPermittedException;
import com.innov.workflow.activiti.service.runtime.PermissionService;
import com.innov.workflow.activiti.util.TaskUtil;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ActivitiTaskActionService {
    private static final Logger logger = LoggerFactory.getLogger(ActivitiTaskActionService.class);
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected PermissionService permissionService;
    @Autowired
    protected IdentityService identityService;
    @Autowired
    protected HistoryService historyService;


    public ActivitiTaskActionService() {
    }

    public void completeTask(String taskId) {
        User currentUser = identityService.getCurrentUserObject();
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new NotFoundException("Task with id: " + taskId + " does not exist");
        } else if (!this.permissionService.isTaskOwnerOrAssignee(currentUser, task) && !this.permissionService.validateIfUserIsInitiatorAndCanCompleteTask(currentUser, task)) {
            throw new NotPermittedException();
        } else {
            try {
                this.taskService.complete(task.getId());
            } catch (ActivitiException var5) {
                logger.error("Error completing task " + taskId, var5);
                throw new BadRequestException("Task " + taskId + " can't be completed", var5);
            }
        }
    }

    public TaskRepresentation assignTask(String taskId, ObjectNode requestNode) {
        User currentUser = identityService.getCurrentUserObject();
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new NotFoundException("Task with id: " + taskId + " does not exist");
        } else {
            this.checkTaskPermissions(taskId, currentUser, task);
            if (requestNode.get("assignee") != null) {
                String assigneeIdString = requestNode.get("assignee").asText();
                User cachedUser = this.identityService.getUser(assigneeIdString);
                if (cachedUser == null) {
                    throw new BadRequestException("Invalid assignee id");
                } else {
                    this.assignTask(currentUser, task, assigneeIdString);
                    task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
                    TaskRepresentation rep = new TaskRepresentation(task);
                    TaskUtil.fillPermissionInformation(rep, task, currentUser, this.identityService, this.historyService, this.repositoryService);
                    this.populateAssignee(task, rep);
                    rep.setInvolvedPeople(this.getInvolvedUsers(taskId));
                    return rep;
                }
            } else {
                throw new BadRequestException("Assignee is required");
            }
        }
    }

    public void involveUser(String taskId, ObjectNode requestNode) {
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new NotFoundException("Task with id: " + taskId + " does not exist");
        } else {
            User currentUser = identityService.getCurrentUserObject();
            this.permissionService.validateReadPermissionOnTask(currentUser, task.getId());
            if (requestNode.get("userId") != null) {
                String userId = requestNode.get("userId").asText();
                User user = this.identityService.getUser(userId);
                if (user == null) {
                    throw new BadRequestException("Invalid user id");
                } else {
                    this.taskService.addUserIdentityLink(taskId, userId, "participant");
                }
            } else {
                throw new BadRequestException("User id is required");
            }
        }
    }

    public void removeInvolvedUser(String taskId, ObjectNode requestNode) {
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new NotFoundException("Task with id: " + taskId + " does not exist");
        } else {
            this.permissionService.validateReadPermissionOnTask(identityService.getCurrentUserObject(), task.getId());
            String assigneeString = null;
            String userId;
            if (requestNode.get("userId") != null) {
                userId = requestNode.get("userId").asText();
                if (this.identityService.getUser(userId) == null) {
                    throw new BadRequestException("Invalid user id");
                }

                assigneeString = String.valueOf(userId);
            } else {
                if (requestNode.get("email") == null) {
                    throw new BadRequestException("User id or email is required");
                }

                userId = requestNode.get("email").asText();
                assigneeString = userId;
            }

            this.taskService.deleteUserIdentityLink(taskId, assigneeString, "participant");
        }
    }

    public void claimTask(String taskId) {
        User currentUser = identityService.getCurrentUserObject();
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new NotFoundException("Task with id: " + taskId + " does not exist");
        } else {
            this.permissionService.validateReadPermissionOnTask(currentUser, task.getId());

            try {
                this.taskService.claim(task.getId(), String.valueOf(currentUser.getId()));
            } catch (ActivitiException var5) {
                throw new BadRequestException("Task " + taskId + " can't be claimed", var5);
            }
        }
    }

    protected void checkTaskPermissions(String taskId, User currentUser, Task task) {
        this.permissionService.validateReadPermissionOnTask(currentUser, task.getId());
    }

    protected String validateEmail(ObjectNode requestNode) {
        String email = requestNode.get("email") != null ? requestNode.get("email").asText() : null;
        if (email == null) {
            throw new BadRequestException("Email is mandatory");
        } else {
            return email;
        }
    }

    protected void assignTask(User currentUser, Task task, String assigneeIdString) {
        try {
            String oldAssignee = task.getAssignee();
            this.taskService.setAssignee(task.getId(), assigneeIdString);
            this.addIdentiyLinkForUser(task, oldAssignee, "participant");
            String currentUserIdString = String.valueOf(currentUser.getId());
            this.addIdentiyLinkForUser(task, currentUserIdString, "participant");
        } catch (ActivitiException var6) {
            throw new BadRequestException("Task " + task.getId() + " can't be assigned", var6);
        }
    }

    protected void addIdentiyLinkForUser(Task task, String userId, String linkType) {
        List<IdentityLink> identityLinks = this.taskService.getIdentityLinksForTask(task.getId());
        boolean isOldUserInvolved = false;
        Iterator i$ = identityLinks.iterator();

        while (true) {
            IdentityLink identityLink;
            do {
                do {
                    if (!i$.hasNext()) {
                        if (!isOldUserInvolved) {
                            this.taskService.addUserIdentityLink(task.getId(), userId, linkType);
                        }

                        return;
                    }

                    identityLink = (IdentityLink) i$.next();
                } while (!userId.equals(identityLink.getUserId()));
            } while (!identityLink.getType().equals("participant") && !identityLink.getType().equals("candidate"));

            isOldUserInvolved = true;
        }
    }

    protected void populateAssignee(TaskInfo task, TaskRepresentation rep) {
        if (task.getAssignee() != null) {
            User cachedUser = this.identityService.getUser(task.getAssignee());
            if (cachedUser != null) {
                rep.setAssignee(new UserRepresentation(cachedUser));
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
                User cachedUser = this.identityService.getUser(link.getUserId());
                if (cachedUser != null) {
                    result.add(new UserRepresentation(cachedUser));
                }
            }
        }

        return result;
    }
}
