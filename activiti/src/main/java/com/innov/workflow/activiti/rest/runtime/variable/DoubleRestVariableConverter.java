package com.innov.workflow.activiti.rest.runtime.variable;

import com.innov.workflow.activiti.model.runtime.RestVariable;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.springframework.stereotype.Component;

@Component
public class DoubleRestVariableConverter implements RestVariableConverter {
    public DoubleRestVariableConverter() {
    }

    public String getRestTypeName() {
        return "double";
    }

    public Class<?> getVariableType() {
        return Double.class;
    }

    public Object getVariableValue(RestVariable result) {
        if (result.getValue() != null) {
            if (!(result.getValue() instanceof Number)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert doubles");
            } else {
                return ((Number) result.getValue()).doubleValue();
            }
        } else {
            return null;
        }
    }

    public void convertVariableValue(Object variableValue, RestVariable result) {
        if (variableValue != null) {
            if (!(variableValue instanceof Double)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert doubles");
            }

            result.setValue(variableValue);
        } else {
            result.setValue((Object) null);
        }

    }
}