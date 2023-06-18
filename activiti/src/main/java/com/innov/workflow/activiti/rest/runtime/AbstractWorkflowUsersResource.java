package com.innov.workflow.activiti.rest.runtime;

import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.idm.UserRepresentation;
import com.innov.workflow.activiti.old.service.IdentityService;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.IdentityLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AbstractWorkflowUsersResource {
    private static final int MAX_PEOPLE_SIZE = 50;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    public AbstractWorkflowUsersResource() {
    }

    public ResultListDataRepresentation getUsers(String filter, String email, String excludeTaskId, String excludeProcessId, Long groupId) {
        int page = 0;
        int pageSize = 50;
//        UserQuery userQuery = this.identityService.createUserQuery();
//        if (StringUtils.isNotEmpty(filter)) {
//            userQuery.userFullNameLike("%" + filter + "%");
//        }

        List<User> matchingUsers = identityService.getUserLike("%" + filter + "%");
        if (excludeTaskId != null) {
            this.filterUsersInvolvedInTask(excludeTaskId, matchingUsers);
        } else if (excludeProcessId != null) {
            this.filterUsersInvolvedInProcess(excludeProcessId, matchingUsers);
        }

        List<UserRepresentation> userRepresentations = new ArrayList(matchingUsers.size());
        Iterator i$ = matchingUsers.iterator();

        while (i$.hasNext()) {
            User user = (User) i$.next();
            userRepresentations.add(new UserRepresentation(user));
        }

        ResultListDataRepresentation result = new ResultListDataRepresentation(userRepresentations);
        if (page != 0 || page == 0 && matchingUsers.size() == pageSize) {
            result.setTotal((long) matchingUsers.size());
        }

        return result;
    }

    protected void filterUsersInvolvedInProcess(String excludeProcessId, List<User> matchingUsers) {
        Set<String> involvedUsers = this.getInvolvedUsersAsSet(this.runtimeService.getIdentityLinksForProcessInstance(excludeProcessId));
        this.removeinvolvedUsers(matchingUsers, involvedUsers);
    }

    protected void filterUsersInvolvedInTask(String excludeTaskId, List<User> matchingUsers) {
        Set<String> involvedUsers = this.getInvolvedUsersAsSet(this.taskService.getIdentityLinksForTask(excludeTaskId));
        this.removeinvolvedUsers(matchingUsers, involvedUsers);
    }

    protected Set<String> getInvolvedUsersAsSet(List<IdentityLink> involvedPeople) {
        Set<String> involved = null;
        if (involvedPeople.size() > 0) {
            involved = new HashSet();
            Iterator i$ = involvedPeople.iterator();

            while (i$.hasNext()) {
                IdentityLink link = (IdentityLink) i$.next();
                if (link.getUserId() != null) {
                    involved.add(link.getUserId());
                }
            }
        }

        return involved;
    }

    protected void removeinvolvedUsers(List<User> matchingUsers, Set<String> involvedUsers) {
        if (involvedUsers != null) {
            Iterator<User> userIt = matchingUsers.iterator();

            while (userIt.hasNext()) {
                if (involvedUsers.contains(((User) userIt.next()).getId().toString())) {
                    userIt.remove();
                }
            }
        }

    }
}