package com.innov.workflow.activiti.rest.editor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.model.common.ResultListDataRepresentation;
import com.innov.workflow.activiti.model.editor.form.FormRepresentation;
import com.innov.workflow.activiti.repository.editor.ModelRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.*;

@RestController
@RequestMapping({"/api/activiti/form-models"})
public class FormsResource {
    private static final Logger logger = LoggerFactory.getLogger(FormsResource.class);
    private static final int MIN_FILTER_LENGTH = 2;
    @Autowired
    protected ModelRepository modelRepository;
    @Autowired
    protected ObjectMapper objectMapper;

    public FormsResource() {
    }

    @RequestMapping(
            method = {RequestMethod.GET},
            produces = {"application/json"}
    )
    public ResultListDataRepresentation getForms(HttpServletRequest request) {
        String filter = null;
        List<NameValuePair> params = URLEncodedUtils.parse(request.getQueryString(), Charset.forName("UTF-8"));
        NameValuePair nameValuePair;
        if (params != null) {
            Iterator i$ = params.iterator();

            while (i$.hasNext()) {
                nameValuePair = (NameValuePair) i$.next();
                if ("filter".equalsIgnoreCase(nameValuePair.getName())) {
                    filter = nameValuePair.getValue();
                }
            }
        }

        String validFilter = this.makeValidFilterText(filter);
        nameValuePair = null;
        List models;
        if (validFilter != null) {
            models = this.modelRepository.findModelsByModelType(2, validFilter);
        } else {
            models = this.modelRepository.findModelsByModelType(2);
        }

        List<FormRepresentation> reps = new ArrayList();
        Iterator i$ = models.iterator();

        while (i$.hasNext()) {
            Model model = (Model) i$.next();
            reps.add(new FormRepresentation(model));
        }

        Collections.sort(reps, new NameComparator());
        ResultListDataRepresentation result = new ResultListDataRepresentation(reps);
        result.setTotal((long) models.size());
        return result;
    }

    protected String makeValidFilterText(String filterText) {
        String validFilter = null;
        if (filterText != null) {
            String trimmed = StringUtils.trim(filterText);
            if (trimmed.length() >= 2) {
                validFilter = "%" + trimmed.toLowerCase() + "%";
            }
        }

        return validFilter;
    }

    class NameComparator implements Comparator<FormRepresentation> {
        NameComparator() {
        }

        public int compare(FormRepresentation o1, FormRepresentation o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }
}
