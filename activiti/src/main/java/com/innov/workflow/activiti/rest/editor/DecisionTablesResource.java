package com.innov.workflow.activiti.rest.editor;


import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.service.editor.ActivitiDecisionTableService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping({"/api/activiti/decision-table-models"})
public class DecisionTablesResource {
    @Autowired
    protected ActivitiDecisionTableService decisionTableService;

    public DecisionTablesResource() {
    }

    @RequestMapping(
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public ResultListDataRepresentation getDecisionTables(HttpServletRequest request) {
        String filter = null;
        List<NameValuePair> params = URLEncodedUtils.parse(request.getQueryString(), Charset.forName("UTF-8"));
        if (params != null) {
            Iterator i$ = params.iterator();

            while (i$.hasNext()) {
                NameValuePair nameValuePair = (NameValuePair) i$.next();
                if ("filter".equalsIgnoreCase(nameValuePair.getName())) {
                    filter = nameValuePair.getValue();
                }
            }
        }

        return this.decisionTableService.getDecisionTables(filter);
    }
}
