package com.innov.workflow.activiti.model.runtime;

import com.innov.workflow.activiti.domain.runtime.RelatedContent;
import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import com.innov.workflow.activiti.model.component.SimpleContentTypeMapper;
import lombok.Data;

import java.util.Date;


@Data
public class RelatedContentRepresentation extends AbstractRepresentation {
    public String previewStatus = "queued";
    public String thumbnailStatus = "queued";
    protected Long id;
    protected String name;
    protected Date created;
    protected String createdBy;
    protected boolean contentAvailable;
    protected boolean link;
    protected String source;
    protected String sourceId;
    protected String mimeType;
    protected String simpleType;
    protected String linkUrl;

    public RelatedContentRepresentation() {
    }

    public RelatedContentRepresentation(RelatedContent content, SimpleContentTypeMapper mapper) {
        this.id = content.getId();
        this.name = content.getName();
        this.created = content.getCreated();
        this.createdBy = content.getCreatedBy();
        this.contentAvailable = content.isContentAvailable();
        this.mimeType = content.getMimeType();
        this.link = content.isLink();
        this.linkUrl = content.getLinkUrl();
        this.source = content.getSource();
        this.sourceId = content.getSourceId();
        if (mapper != null) {
            this.simpleType = mapper.getSimpleType(content);
        }
    }
}
