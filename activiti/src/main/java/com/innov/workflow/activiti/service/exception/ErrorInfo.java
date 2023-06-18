package com.innov.workflow.activiti.service.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

public class ErrorInfo {
    private String message;
    private String messageKey;
    private Map<String, Object> customData;

    public ErrorInfo(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getMessageKey() {
        return this.messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Map<String, Object> getCustomData() {
        return this.customData;
    }

    public void setCustomData(Map<String, Object> params) {
        this.customData = params;
    }

    public void addParameter(String name, Object value) {
        if (this.customData == null) {
            this.customData = new HashMap();
        }

        this.customData.put(name, value);
    }
}
