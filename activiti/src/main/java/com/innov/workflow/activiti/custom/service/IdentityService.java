package com.innov.workflow.activiti.custom.service;


import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.domain.entity.Organization;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.exception.ApiException;
import com.innov.workflow.core.service.GroupService;
import com.innov.workflow.core.service.UserService;
import com.innov.workflow.idm.config.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IdentityService {

    private final UserService userService;

    private final GroupService groupService;
    private final JwtUtils jwtUtils;

    public User getCurrentUserObject() {
        User user = jwtUtils.getCurrentUser();
        if (user == null) throw new ApiException(HttpStatus.UNAUTHORIZED, "User need to be authenticated");
        return user;
    }

    public List<String> getGroupsIds(User user) {

        List<String> groupIds = new ArrayList<>();
        if (user != null) {
            for (Group group : user.getGroups()) {
                groupIds.add(group.getId().toString());
            }
        }
        return groupIds;
    }

    public List<User> getUsersLike(String pattern) {
        return userService.getUserByUsernameLike(pattern);
    }

    public List<Group> getGroupsLike(String pattern) {
        return groupService.getGroupsByNameLike(pattern);
    }

    public List<Group> getGroupsLikeFromOrganization(String pattern, Organization organization) {
        return groupService.getGroupsByNameLikeAndOrganization(pattern, organization);
    }

    public User getUser(String assignee) {
        return userService.getUserByUsername(assignee);
    }

    public User saveUser(User user) {
        return userService.saveUser(user);
    }
}
