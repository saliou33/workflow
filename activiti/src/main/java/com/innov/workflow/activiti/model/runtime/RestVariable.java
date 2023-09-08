package com.innov.workflow.activiti.model.runtime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public class RestVariable {
    private String name;
    private String type;
    private RestVariableScope variableScope;
    private Object value;
    private String valueUrl;

    public RestVariable() {
    }

    public static RestVariableScope getScopeFromString(String scope) {
        if (scope != null) {
            RestVariableScope[] arr$ = RestVariable.RestVariableScope.values();
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                RestVariableScope s = arr$[i$];
                if (s.name().equalsIgnoreCase(scope)) {
                    return s;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public RestVariableScope getVariableScope() {
        return this.variableScope;
    }

    public void setVariableScope(RestVariableScope variableScope) {
        this.variableScope = variableScope;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getScope() {
        String scope = null;
        if (this.variableScope != null) {
            scope = this.variableScope.name().toLowerCase();
        }

        return scope;
    }

    public void setScope(String scope) {
        this.setVariableScope(getScopeFromString(scope));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getValueUrl() {
        return this.valueUrl;
    }

    public void setValueUrl(String valueUrl) {
        this.valueUrl = valueUrl;
    }

    public enum RestVariableScope {
        LOCAL,
        GLOBAL;

        RestVariableScope() {
        }
    }
}
