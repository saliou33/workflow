package com.innov.workflow.activiti.domain.editor;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ACT_DE_MODEL_HISTORY")
@Data
public class ModelHistory extends AbstractModel {
    @Column(name = "model_id")
    protected String modelId;
    @Column(name = "removal_date")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date removalDate;
}
