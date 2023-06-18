package com.innov.workflow.activiti.service.editor.mapper;

import org.activiti.bpmn.model.ReceiveTask;

public class ReceiveTaskInfoMapper extends AbstractInfoMapper {
    public ReceiveTaskInfoMapper() {
    }

    protected void mapProperties(Object element) {
        ReceiveTask receiveTask = (ReceiveTask) element;
        this.createListenerPropertyNodes("Execution listeners", receiveTask.getExecutionListeners());
    }
}
