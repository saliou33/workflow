package com.innov.workflow.activiti.rest.runtime.variable;

import com.innov.workflow.activiti.model.runtime.RestVariable;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.springframework.stereotype.Component;

@Component
public class ShortRestVariableConverter implements RestVariableConverter {
    public ShortRestVariableConverter() {
    }

    public String getRestTypeName() {
        return "short";
    }

    public Class<?> getVariableType() {
        return Short.class;
    }

    public Object getVariableValue(RestVariable result) {
        if (result.getValue() != null) {
            if (!(result.getValue() instanceof Number)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert shorts");
            } else {
                return ((Number) result.getValue()).shortValue();
            }
        } else {
            return null;
        }
    }

    public void convertVariableValue(Object variableValue, RestVariable result) {
        if (variableValue != null) {
            if (!(variableValue instanceof Short)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert shorts");
            }

            result.setValue(variableValue);
        } else {
            result.setValue(null);
        }

    }
}
