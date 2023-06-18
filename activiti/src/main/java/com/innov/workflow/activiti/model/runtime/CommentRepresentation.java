package com.innov.workflow.activiti.model.runtime;

import com.innov.workflow.activiti.domain.runtime.Comment;
import com.innov.workflow.activiti.model.common.AbstractRepresentation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class CommentRepresentation extends AbstractRepresentation {
    private Long id;
    private String message;
    private Date created;
    private String createdBy;

    public CommentRepresentation(Comment comment) {
        this.id = comment.getId();
        this.message = comment.getMessage();
        this.created = comment.getCreated();
        this.createdBy = comment.getCreatedBy();
    }
}
