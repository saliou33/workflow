package com.innov.workflow.activiti.model.editor;

import com.innov.workflow.activiti.domain.editor.AbstractModel;
import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.domain.editor.ModelHistory;
import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;

import java.util.Date;

@Data
public class ModelRepresentation extends AbstractRepresentation {
    protected String id;
    protected String name;
    protected String key;
    protected String description;
    protected String createdBy;
    protected String lastUpdatedBy;
    protected Date lastUpdated;
    protected boolean latestVersion;
    protected int version;
    protected String comment;
    protected Integer modelType;

    public ModelRepresentation(AbstractModel model) {
        this.initialize(model);
    }

    public ModelRepresentation() {
    }

    public void initialize(AbstractModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.key = model.getKey();
        this.description = model.getDescription();
        this.createdBy = model.getCreatedBy();
        this.lastUpdated = model.getLastUpdated();
        this.version = model.getVersion();
        this.lastUpdatedBy = model.getLastUpdatedBy();
        this.comment = model.getComment();
        this.modelType = model.getModelType();
        if (model instanceof Model) {
            this.setLatestVersion(true);
        } else if (model instanceof ModelHistory) {
            this.setLatestVersion(false);
        }

    }

    public Model toModel() {
        Model model = new Model();
        model.setName(this.name);
        model.setDescription(this.description);
        return model;
    }

    public void updateModel(Model model) {
        model.setDescription(this.description);
        model.setName(this.name);
    }
}
