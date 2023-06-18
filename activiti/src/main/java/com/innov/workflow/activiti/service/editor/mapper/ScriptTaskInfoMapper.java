package com.innov.workflow.activiti.service.editor.mapper;

import org.activiti.bpmn.model.ScriptTask;
import org.apache.commons.lang3.StringUtils;

public class ScriptTaskInfoMapper extends AbstractInfoMapper {
    public ScriptTaskInfoMapper() {
    }

    protected void mapProperties(Object element) {
        ScriptTask scriptTask = (ScriptTask) element;
        if (StringUtils.isNotEmpty(scriptTask.getScriptFormat())) {
            this.createPropertyNode("Script format", scriptTask.getScriptFormat());
        }

        if (StringUtils.isNotEmpty(scriptTask.getScript())) {
            this.createPropertyNode("Script", scriptTask.getScript());
        }

        this.createListenerPropertyNodes("Execution listeners", scriptTask.getExecutionListeners());
    }
}
