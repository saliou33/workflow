package com.innov.workflow.activiti.domain.editor;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@MappedSuperclass
public class AbstractModel {
    public static final int MODEL_TYPE_BPMN = 0;
    public static final int MODEL_TYPE_FORM = 2;
    public static final int MODEL_TYPE_APP = 3;
    public static final int MODEL_TYPE_DECISION_TABLE = 4;
    @Id
    @GeneratedValue(
            generator = "modelIdGenerator"
    )
    @GenericGenerator(
            name = "modelIdGenerator",
            strategy = "uuid2"
    )
    @Column(
            name = "id",
            unique = true
    )
    protected String id;
    @Column(name = "name")
    protected String name;
    @Column(name = "model_key")
    protected String key;
    @Column(name = "description")
    protected String description;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    protected Date created = new Date();
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated")
    protected Date lastUpdated;
    @Column(name = "version")
    protected int version;
    @Column(name = "model_editor_json")
    @Lob
    protected String modelEditorJson;
    @Column(name = "model_comment")
    protected String comment;
    @Column(name = "model_type")
    protected Integer modelType;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

}
