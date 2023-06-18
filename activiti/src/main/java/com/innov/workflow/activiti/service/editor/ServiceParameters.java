package com.innov.workflow.activiti.service.editor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class ServiceParameters {
    protected Map<String, Object> parameters = new HashMap();
    protected Set<String> validParameterNames;

    public ServiceParameters() {
    }

    public static ServiceParameters fromHttpRequest(HttpServletRequest request) {
        ServiceParameters parameters = new ServiceParameters();
        String value = null;
        String name = null;
        Enumeration<String> params = request.getParameterNames();

        while (params.hasMoreElements()) {
            name = (String) params.nextElement();
            value = request.getParameter(name);
            if (value != null && StringUtils.isNotEmpty(value)) {
                parameters.addParameter(name, value);
            }
        }

        return parameters;
    }

    public static ServiceParameters fromObjectNode(ObjectNode node) {
        ServiceParameters parameters = new ServiceParameters();
        Iterator<String> ir = node.fieldNames();
        String name = null;
        JsonNode value = null;

        while (ir.hasNext()) {
            name = (String) ir.next();
            value = node.get(name);
            if (value != null) {
                if (value.isNumber()) {
                    parameters.addParameter(name, value.numberValue());
                } else if (value.isBoolean()) {
                    parameters.addParameter(name, value.booleanValue());
                } else if (value.isTextual()) {
                    parameters.addParameter(name, value.textValue());
                }
            }
        }

        return parameters;
    }

    public void addParameter(String name, Object value) {
        this.parameters.put(name, value);
    }

    public void addValidParameter(String name, Object value) {
        this.parameters.put(name, value);
        this.validParameterNames.add(name);
    }

    public Object getParameter(String name) {
        return this.parameters.get(name);
    }

    public boolean isParameterSet(String name) {
        return this.parameters.containsKey(name);
    }

    public void addValidParameterNames(String[] validParameters) {
        if (this.validParameterNames == null) {
            this.validParameterNames = new HashSet();
        }

        this.validParameterNames.addAll(Arrays.asList(validParameters));
    }

    public Map<String, Object> getValidParameterMap() {
        if (this.validParameterNames == null) {
            return Collections.unmodifiableMap(this.parameters);
        } else {
            Map<String, Object> result = new HashMap();
            Iterator i$ = this.parameters.entrySet().iterator();

            while (i$.hasNext()) {
                Map.Entry<String, Object> parameter = (Map.Entry) i$.next();
                if (this.validParameterNames.contains(parameter.getKey())) {
                    result.put(parameter.getKey(), parameter.getValue());
                }
            }

            return result;
        }
    }
}
