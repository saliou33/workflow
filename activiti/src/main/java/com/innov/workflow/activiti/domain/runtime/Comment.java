package com.innov.workflow.activiti.domain.runtime;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(
        name = "ACT_WO_COMMENTS"
)
@Cache(
        usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE
)
@Data
public class Comment implements Serializable {
    public static final String PROPERTY_CREATED = "created";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @TableGenerator(
            name = "commentIdGenerator",
            allocationSize = 1000
    )
    @Column(name = "id")
    protected Long id;
    @Length(max = 4000)
    @Column(name = "message")
    protected String message;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    protected Date created;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "task_id")
    private String taskId;
    @Column(name = "proc_inst_id")
    private String processInstanceId;
}
