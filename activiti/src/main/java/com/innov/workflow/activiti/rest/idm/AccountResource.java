package com.innov.workflow.activiti.rest.idm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.model.idm.UserRepresentation;
import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountResource {
    private final IdentityService identityService;


    @PostMapping("/authentication")
    public User authentication(HttpServletRequest request) throws AuthException {
        return identityService.getCurrentUserObject();
    }

    @RequestMapping(
            value = {"/activiti/authenticate",},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public User isAuthenticated(HttpServletRequest request) {
        return identityService.getCurrentUserObject();

    }

    @RequestMapping(
            value = {"/activiti/account"},
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public User getAccount() {
        //  User user = SecurityUtils.getCurrentActivitiAppUser().getUserObject();

       return identityService.getCurrentUserObject();
    }
}
