package com.innov.workflow.activiti.service.runtime;

import com.innov.workflow.activiti.domain.runtime.RelatedContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmittedFormVariables {
    private Map<String, List<RelatedContent>> variableContent;
    private Map<String, Object> variables;

    public SubmittedFormVariables() {
    }

    public Map<String, List<RelatedContent>> getVariableContent() {
        return this.variableContent;
    }

    public void setVariableContent(Map<String, List<RelatedContent>> variableContent) {
        this.variableContent = variableContent;
    }

    public Map<String, Object> getVariables() {
        return this.variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public void addContent(String variableName, RelatedContent content) {
        if (this.variableContent == null) {
            this.variableContent = new HashMap();
        }

        List<RelatedContent> contentList = (List) this.variableContent.get(variableName);
        if (contentList == null) {
            contentList = new ArrayList();
            this.variableContent.put(variableName, contentList);
        }

        if (content != null) {
            ((List) contentList).add(content);
        }

    }

    public boolean hasContent() {
        if (this.variableContent != null) {
            return !this.variableContent.isEmpty();
        } else {
            return false;
        }
    }
}
