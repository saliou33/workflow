package com.innov.workflow.activiti.domain.runtime;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(
        name = "ACT_WO_RELATED_CONTENT"
)
@Cache(
        usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE
)
@Data
@NoArgsConstructor
public class RelatedContent {
    @Id
    @GeneratedValue(
            strategy = GenerationType.TABLE,
            generator = "relatedContentGenerator"
    )
    @TableGenerator(
            name = "relatedContentGenerator",
            allocationSize = 1000
    )
    @Column(name = "id")
    protected Long id;
    @Length(max = 255)
    @Column(name = "name")
    protected String name;
    @Length(max = 255)
    @Column(name = "mime_type")
    protected String mimeType;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    protected Date created;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified")
    protected Date lastModified;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "task_id")
    private String taskId;
    @Column(name = "process_id")
    private String processInstanceId;
    @Column(name = "content_source")
    private String source;
    @Column(name = "source_id")
    private String sourceId;
    @Column(name = "content_available")
    private boolean contentAvailable;
    @Column(name = "locked")
    private boolean locked;
    @Column(name = "lock_date")
    private Date lockDate;
    @Column(name = "lock_exp_date")
    private Date lockExpirationDate;
    @Column(name = "lock_owner")
    private String lockOwner;
    @Column(name = "checked_out")
    private boolean checkedOut;
    @Column(name = "checked_out_to_local")
    private boolean checkedOutToLocal;
    @Column(name = "checkout_date")
    private Date checkoutDate;
    @Column(name = "store_id")
    private String contentStoreId;
    @Column(name = "checkout_owner")
    private String checkoutOwner;
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
    @Column(name = "field")
    private String field;
    @Column(name = "related_content")
    private boolean relatedContent = false;
    @Column(name = "link")
    private boolean link = false;
    @Column(name = "link_url")
    private String linkUrl;
    @Column(name = "content_size")
    private Long contentSize;
}
