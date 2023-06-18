package com.innov.workflow.activiti.domain.editor;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(
        name = "ACT_DE_MODEL_RELATION"
)
@Data
public class ModelRelation {
    @Id
    @GeneratedValue(
            generator = "modelRelationIdGenerator"
    )
    @GenericGenerator(
            name = "modelRelationIdGenerator",
            strategy = "uuid2"
    )
    @Column(
            name = "id",
            unique = true
    )
    private String id;
    @Column(name = "parent_model_id")
    private String parentModelId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_model_id", insertable = false, updatable = false)
    private Model parentModel;
    @Column(name = "model_id")
    private String modelId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", insertable = false, updatable = false)
    private Model model;
    @Column(name = "relation_type")
    private String type;

    public ModelRelation() {
    }

    public ModelRelation(String parentModelId, String modelId, String type) {
        this.parentModelId = parentModelId;
        this.modelId = modelId;
        this.type = type;
    }
}