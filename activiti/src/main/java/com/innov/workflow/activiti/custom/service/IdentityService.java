package com.innov.workflow.activiti.custom.service;


import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.exception.ApiException;
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

    public List<User> getUserLike(String p) {
        return userService.getAllUserLike(p);
    }

    public User getUser(String assignee) {
        return userService.getUserByUsername(assignee);
    }

    public User saveUser(User user) {
        return userService.saveUser(user);
    }
}
