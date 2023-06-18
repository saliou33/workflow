package com.innov.workflow.activiti.service.editor.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.model.*;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractInfoMapper implements InfoMapper {
    protected DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    protected ObjectMapper objectMapper = new ObjectMapper();
    protected ArrayNode propertiesNode;

    public AbstractInfoMapper() {
    }

    public ArrayNode map(Object element) {
        this.propertiesNode = this.objectMapper.createArrayNode();
        if (element instanceof FlowElement) {
            FlowElement flowElement = (FlowElement) element;
            if (StringUtils.isNotEmpty(flowElement.getDocumentation())) {
                this.createPropertyNode("Documentation", flowElement.getDocumentation());
            }

            if (element instanceof Activity) {
                Activity activity = (Activity) element;
                if (activity.getLoopCharacteristics() != null) {
                    MultiInstanceLoopCharacteristics multiInstanceDef = activity.getLoopCharacteristics();
                    this.createPropertyNode("Multi-instance activity", "");
                    this.createPropertyNode("Sequential", multiInstanceDef.isSequential());
                    if (StringUtils.isNotEmpty(multiInstanceDef.getInputDataItem())) {
                        this.createPropertyNode("Collection", multiInstanceDef.getInputDataItem());
                    }

                    if (StringUtils.isNotEmpty(multiInstanceDef.getElementVariable())) {
                        this.createPropertyNode("Element variable", multiInstanceDef.getElementVariable());
                    }

                    if (StringUtils.isNotEmpty(multiInstanceDef.getLoopCardinality())) {
                        this.createPropertyNode("Loop cardinality", multiInstanceDef.getLoopCardinality());
                    }

                    if (StringUtils.isNotEmpty(multiInstanceDef.getCompletionCondition())) {
                        this.createPropertyNode("Completion condition", multiInstanceDef.getCompletionCondition());
                    }

                    this.createPropertyNode("", "");
                }

                if (StringUtils.isNotEmpty(activity.getDefaultFlow())) {
                    this.createPropertyNode("Default flow", activity.getDefaultFlow());
                }
            }
        }

        this.mapProperties(element);
        return this.propertiesNode;
    }

    protected abstract void mapProperties(Object var1);

    protected void createListenerPropertyNodes(String name, List<ActivitiListener> listeners) {
        if (CollectionUtils.isNotEmpty(listeners)) {
            List<String> listenerValues = new ArrayList();

            StringBuilder listenerBuilder;
            for (Iterator i$ = listeners.iterator(); i$.hasNext(); listenerValues.add(listenerBuilder.toString())) {
                ActivitiListener listener = (ActivitiListener) i$.next();
                listenerBuilder = new StringBuilder();
                listenerBuilder.append(listener.getEvent());
                if (StringUtils.isNotEmpty(listener.getImplementation())) {
                    listenerBuilder.append(" - ");
                    listenerBuilder.append(listener.getImplementation());
                    listenerBuilder.append(" (");
                    listenerBuilder.append(listener.getImplementationType());
                    listenerBuilder.append(")");
                }

                if (CollectionUtils.isNotEmpty(listener.getFieldExtensions())) {
                    listenerBuilder.append(", field extensions: ");

                    for (int i = 0; i < listener.getFieldExtensions().size(); ++i) {
                        if (i > 0) {
                            listenerBuilder.append(",  ");
                        }

                        FieldExtension field = (FieldExtension) listener.getFieldExtensions().get(i);
                        listenerBuilder.append(field.getFieldName());
                        if (StringUtils.isNotEmpty(field.getStringValue())) {
                            listenerBuilder.append(" - ");
                            listenerBuilder.append(field.getStringValue());
                        } else if (StringUtils.isNotEmpty(field.getExpression())) {
                            listenerBuilder.append(" - ");
                            listenerBuilder.append(field.getExpression());
                        }
                    }
                }
            }

            this.createPropertyNode(name, (List) listenerValues);
        }

    }

    protected void createFieldPropertyNodes(String name, List<FieldExtension> fields) {
        if (CollectionUtils.isNotEmpty(fields)) {
            List<String> fieldValues = new ArrayList();

            StringBuilder fieldBuilder;
            for (Iterator i$ = fields.iterator(); i$.hasNext(); fieldValues.add(fieldBuilder.toString())) {
                FieldExtension field = (FieldExtension) i$.next();
                fieldBuilder = new StringBuilder();
                fieldBuilder.append(field.getFieldName());
                if (StringUtils.isNotEmpty(field.getStringValue())) {
                    fieldBuilder.append(" - ");
                    fieldBuilder.append(field.getStringValue());
                } else if (StringUtils.isNotEmpty(field.getExpression())) {
                    fieldBuilder.append(" - ");
                    fieldBuilder.append(field.getExpression());
                }
            }

            this.createPropertyNode(name, (List) fieldValues);
        }

    }

    protected void createPropertyNode(String name, String value) {
        if (StringUtils.isNotEmpty(value)) {
            ObjectNode propertyNode = this.objectMapper.createObjectNode();
            propertyNode.put("name", name);
            propertyNode.put("value", value);
            this.propertiesNode.add(propertyNode);
        }

    }

    protected void createPropertyNode(String name, Date value) {
        if (value != null) {
            this.createPropertyNode(name, this.dateFormat.format(value));
        }

    }

    protected void createPropertyNode(String name, Boolean value) {
        if (value != null) {
            this.createPropertyNode(name, value.toString());
        }

    }

    protected void createPropertyNode(String name, List<String> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            ArrayNode itemsNode = this.objectMapper.createArrayNode();
            Iterator i$ = values.iterator();

            while (i$.hasNext()) {
                String value = (String) i$.next();
                itemsNode.add(value);
            }

            ObjectNode propertyNode = this.objectMapper.createObjectNode();
            propertyNode.put("name", name);
            propertyNode.put("type", "list");
            propertyNode.put("value", itemsNode);
            this.propertiesNode.add(propertyNode);
        }

    }
}
