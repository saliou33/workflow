package com.innov.workflow.activiti.rest.runtime.variable;

import com.innov.workflow.activiti.model.runtime.RestVariable;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.springframework.stereotype.Component;

@Component
public class LongRestVariableConverter implements RestVariableConverter {
    public LongRestVariableConverter() {
    }

    public String getRestTypeName() {
        return "long";
    }

    public Class<?> getVariableType() {
        return Long.class;
    }

    public Object getVariableValue(RestVariable result) {
        if (result.getValue() != null) {
            if (!(result.getValue() instanceof Number)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert longs");
            } else {
                return ((Number) result.getValue()).longValue();
            }
        } else {
            return null;
        }
    }

    public void convertVariableValue(Object variableValue, RestVariable result) {
        if (variableValue != null) {
            if (!(variableValue instanceof Long)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert integers");
            }

            result.setValue(variableValue);
        } else {
            result.setValue(null);
        }

    }
}
