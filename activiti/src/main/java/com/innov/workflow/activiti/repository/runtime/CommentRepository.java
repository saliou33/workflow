package com.innov.workflow.activiti.repository.runtime;

import com.innov.workflow.activiti.domain.runtime.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskId(String var1, Sort var2);

    List<Comment> findByProcessInstanceId(String var1, Sort var2);

    long countByTaskId(String var1);

    long countByProcessInstanceId(String var1);

    @Modifying
    @Query("delete from Comment c where c.processInstanceId = :processInstanceId")
    void deleteAllByProcessInstanceId(@Param("processInstanceId") String var1);
}