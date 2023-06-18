package com.innov.workflow.activiti.rest.runtime.variable;

import com.innov.workflow.activiti.model.runtime.RestVariable;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class DateRestVariableConverter implements RestVariableConverter {
    protected ISO8601DateFormat isoFormatter = new ISO8601DateFormat();

    public DateRestVariableConverter() {
    }

    public String getRestTypeName() {
        return "date";
    }

    public Class<?> getVariableType() {
        return Date.class;
    }

    public Object getVariableValue(RestVariable result) {
        if (result.getValue() != null) {
            if (!(result.getValue() instanceof String)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert string to date");
            } else {
                try {
                    return this.isoFormatter.parse((String) result.getValue());
                } catch (ParseException var3) {
                    throw new ActivitiIllegalArgumentException("The given variable value is not a date: '" + result.getValue() + "'", var3);
                }
            }
        } else {
            return null;
        }
    }

    public void convertVariableValue(Object variableValue, RestVariable result) {
        if (variableValue != null) {
            if (!(variableValue instanceof Date)) {
                throw new ActivitiIllegalArgumentException("Converter can only convert booleans");
            }

            result.setValue(this.isoFormatter.format(variableValue));
        } else {
            result.setValue((Object) null);
        }

    }
}