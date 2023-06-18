package com.innov.workflow.activiti.service.editor.mapper;

import org.activiti.bpmn.model.SequenceFlow;
import org.apache.commons.lang3.StringUtils;

public class SequenceFlowInfoMapper extends AbstractInfoMapper {
    public SequenceFlowInfoMapper() {
    }

    protected void mapProperties(Object element) {
        SequenceFlow sequenceFlow = (SequenceFlow) element;
        if (StringUtils.isNotEmpty(sequenceFlow.getConditionExpression())) {
            this.createPropertyNode("Condition expression", sequenceFlow.getConditionExpression());
        }

        this.createListenerPropertyNodes("Execution listeners", sequenceFlow.getExecutionListeners());
    }
}
