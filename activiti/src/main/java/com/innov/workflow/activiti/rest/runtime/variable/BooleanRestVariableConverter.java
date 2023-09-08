package com.innov.workflow.activiti.rest.runtime.variable;

import com.innov.workflow.activiti.model.runtime.RestVariable;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.springframework.stereotype.Component;

@Component
public class BooleanRestVariableConverter implements RestVariableConverter {
    public BooleanRestVariableConverter() {
    }

    public String getRestTypeName() {
        return "boolean";
    }

    public Class<?> getVariableType() {
        return Boolean.class;
    }

    public Object getVariableValue(RestVariable result) {
        if (result.getValue() != null) {
            if (!(result.getValue() instanceof Boolean)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert booleans");
            } else {
                return result.getValue();
            }
        } else {
            return null;
        }
    }

    public void convertVariableValue(Object variableValue, RestVariable result) {
        if (variableValue != null) {
            if (!(variableValue instanceof Boolean)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert booleans");
            }

            result.setValue(variableValue);
        } else {
            result.setValue(null);
        }

    }
}
