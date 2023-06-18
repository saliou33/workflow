package com.innov.workflow.activiti.service.editor.mapper;

import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ServiceTask;

public class ServiceTaskInfoMapper extends AbstractInfoMapper {
    public ServiceTaskInfoMapper() {
    }

    protected void mapProperties(Object element) {
        ServiceTask serviceTask = (ServiceTask) element;
        if (ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(serviceTask.getImplementationType())) {
            this.createPropertyNode("Class", serviceTask.getImplementation());
        } else if (ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equals(serviceTask.getImplementationType())) {
            this.createPropertyNode("Expression", serviceTask.getImplementation());
        } else if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equals(serviceTask.getImplementationType())) {
            this.createPropertyNode("Delegate expression", serviceTask.getImplementation());
        }

        if (serviceTask.isAsynchronous()) {
            this.createPropertyNode("Asynchronous", true);
            this.createPropertyNode("Exclusive", !serviceTask.isNotExclusive());
        }

        if ("mail".equalsIgnoreCase(serviceTask.getType())) {
            this.createPropertyNode("Type", "Mail task");
        }

        this.createPropertyNode("Result variable name", serviceTask.getResultVariableName());
        this.createFieldPropertyNodes("Field extensions", serviceTask.getFieldExtensions());
        this.createListenerPropertyNodes("Execution listeners", serviceTask.getExecutionListeners());
    }
}
