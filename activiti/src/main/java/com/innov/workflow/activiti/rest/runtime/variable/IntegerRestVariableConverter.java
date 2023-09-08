package com.innov.workflow.activiti.rest.runtime.variable;

import com.innov.workflow.activiti.model.runtime.RestVariable;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.springframework.stereotype.Component;

@Component
public class IntegerRestVariableConverter implements RestVariableConverter {
    public IntegerRestVariableConverter() {
    }

    public String getRestTypeName() {
        return "integer";
    }

    public Class<?> getVariableType() {
        return Integer.class;
    }

    public Object getVariableValue(RestVariable result) {
        if (result.getValue() != null) {
            if (!(result.getValue() instanceof Number)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert integers");
            } else {
                return ((Number) result.getValue()).intValue();
            }
        } else {
            return null;
        }
    }

    public void convertVariableValue(Object variableValue, RestVariable result) {
        if (variableValue != null) {
            if (!(variableValue instanceof Integer)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert integers");
            }

            result.setValue(variableValue);
        } else {
            result.setValue(null);
        }

    }
}
