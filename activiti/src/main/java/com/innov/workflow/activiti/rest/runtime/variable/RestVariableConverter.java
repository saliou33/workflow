package com.innov.workflow.activiti.rest.runtime.variable;

import com.innov.workflow.activiti.model.runtime.RestVariable;

public interface RestVariableConverter {
    String getRestTypeName();

    Class<?> getVariableType();

    Object getVariableValue(RestVariable var1);

    void convertVariableValue(Object var1, RestVariable var2);
}
