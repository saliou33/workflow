package com.innov.workflow.activiti.service.runtime;

import com.innov.workflow.activiti.domain.runtime.Comment;
import com.innov.workflow.activiti.repository.runtime.CommentRepository;
import com.innov.workflow.core.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;


    public CommentService() {
    }

    public Long countCommentsForTask(String taskId) {
        return this.commentRepository.countByTaskId(taskId);
    }

    public Long countCommentsForProcessInstance(String processInstanceId) {
        return this.commentRepository.countByProcessInstanceId(processInstanceId);
    }

    public List<Comment> getCommentsForTask(String taskId, boolean latestFirst) {
        return this.commentRepository.findByTaskId(taskId, Sort.by(latestFirst ? Direction.DESC : Direction.ASC, "created"));
    }

    public List<Comment> getCommentsForProcessInstance(String processInstanceId, boolean latestFirst) {
        return this.commentRepository.findByProcessInstanceId(processInstanceId, Sort.by(latestFirst ? Direction.DESC : Direction.ASC, "created"));
    }

    public Comment createComment(String message, User createdBy, String processInstanceId) {
        return this.createComment(message, createdBy, null, processInstanceId);
    }

    public Comment createComment(String message, User createdBy, String taskId, String processInstanceId) {
        Comment newComment = new Comment();
        newComment.setMessage(message);
        newComment.setCreatedBy(createdBy.getId());
        newComment.setCreated(Date.from(Instant.now()));
        newComment.setTaskId(taskId);
        newComment.setProcessInstanceId(processInstanceId);
        this.commentRepository.save(newComment);
        return newComment;
    }

    public void deleteComment(Comment comment) {
        this.commentRepository.delete(comment);
    }

    @Transactional
    public void deleteAllCommentsForProcessInstance(String processInstanceId) {
        this.commentRepository.deleteAllByProcessInstanceId(processInstanceId);
    }
}