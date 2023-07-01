package com.innov.workflow.activiti.rest.editor;

import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EditorGroupsResource {


    public EditorGroupsResource() {
    }

    @RequestMapping(
            value = {"/api/activiti/editor-groups"},
            method = {RequestMethod.GET}
    )
    public ResultListDataRepresentation getGroups(@RequestParam(required = false, value = "filter") String filter) {
        String groupNameFilter;
//        if (StringUtils.isEmpty(filter)) {
//            groupNameFilter = "%";
//        } else {
//            groupNameFilter = "%" + filter + "%";
//        }
//
//        List<Group> matchingGroups = this.identityService.createGroupQuery().groupNameLike(groupNameFilter).groupType("assignment").list();
        ResultListDataRepresentation result = new ResultListDataRepresentation();
        return result;
    }
}
