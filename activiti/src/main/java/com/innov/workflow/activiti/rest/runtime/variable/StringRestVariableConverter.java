package com.innov.workflow.activiti.rest.runtime.variable;

import com.innov.workflow.activiti.model.runtime.RestVariable;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.springframework.stereotype.Component;

@Component
public class StringRestVariableConverter implements RestVariableConverter {
    public StringRestVariableConverter() {
    }

    public String getRestTypeName() {
        return "string";
    }

    public Class<?> getVariableType() {
        return String.class;
    }

    public Object getVariableValue(RestVariable result) {
        if (result.getValue() != null) {
            if (!(result.getValue() instanceof String)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert strings");
            } else {
                return (String) result.getValue();
            }
        } else {
            return null;
        }
    }

    public void convertVariableValue(Object variableValue, RestVariable result) {
        if (variableValue != null) {
            if (!(variableValue instanceof String)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert strings");
            }

            result.setValue(variableValue);
        } else {
            result.setValue((Object) null);
        }

    }
}
