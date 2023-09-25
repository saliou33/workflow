package com.innov.workflow.activiti.rest.editor;

import com.innov.workflow.activiti.custom.service.IdentityService;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.core.domain.entity.Group;
import com.innov.workflow.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EditorGroupsResource {

    @Autowired
    private IdentityService identityService;

    public EditorGroupsResource() {
    }

    @RequestMapping(
            value = {"/api/activiti/editor-groups"},
            method = {RequestMethod.GET}
    )
    public ResultListDataRepresentation getGroups(@RequestParam(required = false, value = "filter") String filter) {
        String groupNameFilter;
        if (StringUtils.isEmpty(filter)) {
            groupNameFilter = "%";
        } else {
            groupNameFilter = "%" + filter + "%";
        }

        List<Group> matchingGroups = identityService.getGroupsLike(groupNameFilter);

        ResultListDataRepresentation result = new ResultListDataRepresentation(matchingGroups);
        return result;
    }
}
