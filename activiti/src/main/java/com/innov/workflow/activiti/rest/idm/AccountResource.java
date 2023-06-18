package com.innov.workflow.activiti.rest.idm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.model.idm.UserRepresentation;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class AccountResource {
    private final ObjectMapper objectMapper;
    private final UserService userService;


    @PostMapping("/authentication")
    public UserRepresentation authentication(HttpServletRequest request) {
        User user = userService.getUserByUsername("zale");
        UserRepresentation userRepresentation = new UserRepresentation(user);
        return userRepresentation;
    }

    @RequestMapping(
            value = {"/rest/authenticate",},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public UserRepresentation isAuthenticated(HttpServletRequest request) {
        //String user = request.getRemoteUser();


        User user = userService.getUserByUsername("zale");
        UserRepresentation userRepresentation = new UserRepresentation(user);
        return userRepresentation;
    }

    @RequestMapping(
            value = {"/rest/account"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public UserRepresentation getAccount() {
        //  User user = SecurityUtils.getCurrentActivitiAppUser().getUserObject();

        User user = userService.getUserByUsername("zale");
        UserRepresentation userRepresentation = new UserRepresentation(user);
        return userRepresentation;


    }
}
