package com.innov.workflow.activiti.service.api;

import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.domain.editor.ModelHistory;
import com.innov.workflow.activiti.model.editor.ModelKeyRepresentation;
import com.innov.workflow.activiti.model.editor.ModelRepresentation;
import com.innov.workflow.activiti.model.editor.ReviveModelResultRepresentation;
import com.innov.workflow.core.domain.entity.User;
import org.activiti.bpmn.model.BpmnModel;

import java.util.List;
import java.util.Map;

public interface ModelService {
    Model getModel(String var1);

    List<AbstractModel> getModelsByModelType(Integer var1);

    ModelKeyRepresentation validateModelKey(Model var1, Integer var2, String var3);

    ModelHistory getModelHistory(String var1, String var2);

    Long getModelCountForUser(User var1, int var2);

    BpmnModel getBpmnModel(AbstractModel var1);

    byte[] getBpmnXML(BpmnModel var1);

    byte[] getBpmnXML(AbstractModel var1);

    BpmnModel getBpmnModel(AbstractModel var1, Map<String, Model> var2, Map<String, Model> var3);

    Model createModel(ModelRepresentation var1, String var2, User var3);

    Model createModel(Model var1, User var2);

    Model saveModel(Model var1);

    Model saveModel(Model var1, String var2, byte[] var3, boolean var4, String var5, User var6);

    Model saveModel(String var1, String var2, String var3, String var4, String var5, boolean var6, String var7, User var8);

    Model createNewModelVersion(Model var1, String var2, User var3);

    ModelHistory createNewModelVersionAndReturnModelHistory(Model var1, String var2, User var3);

    void deleteModel(String var1, boolean var2, boolean var3);

    ReviveModelResultRepresentation reviveProcessModelHistory(ModelHistory var1, User var2, String var3);
}
