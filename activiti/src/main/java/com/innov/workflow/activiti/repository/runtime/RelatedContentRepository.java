package com.innov.workflow.activiti.repository.runtime;

import com.innov.workflow.activiti.domain.runtime.RelatedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatedContentRepository extends JpaRepository<RelatedContent, Long> {
    Page<RelatedContent> findAllRelatedBySourceAndSourceId(@Param("source") String var1, @Param("sourceId") String var2, Pageable var3);

    @Query("from RelatedContent r where r.taskId = :taskId and r.relatedContent = true")
    Page<RelatedContent> findAllRelatedByTaskId(@Param("taskId") String var1, Pageable var2);

    @Query("from RelatedContent r where r.taskId = :taskId and r.relatedContent = false")
    Page<RelatedContent> findAllFieldBasedContentByTaskId(@Param("taskId") String var1, Pageable var2);

    Page<RelatedContent> findAllByTaskIdAndField(@Param("taskId") String var1, @Param("field") String var2, Pageable var3);

    @Query("from RelatedContent r where r.processInstanceId = :processInstanceId and r.relatedContent = true")
    Page<RelatedContent> findAllRelatedByProcessInstanceId(@Param("processInstanceId") String var1, Pageable var2);

    @Query("from RelatedContent r where r.processInstanceId = :processInstanceId and r.relatedContent = false")
    Page<RelatedContent> findAllFieldBasedContentByProcessInstanceId(@Param("processInstanceId") String var1, Pageable var2);

    @Query("from RelatedContent r where r.processInstanceId = :processInstanceId")
    Page<RelatedContent> findAllContentByProcessInstanceId(@Param("processInstanceId") String var1, Pageable var2);

    @Query("from RelatedContent r where r.processInstanceId = :processInstanceId and r.field = :field")
    Page<RelatedContent> findAllByProcessInstanceIdAndField(@Param("processInstanceId") String var1, @Param("field") String var2, Pageable var3);

    @Modifying
    @Query("delete from RelatedContent r where r.processInstanceId = :processInstanceId")
    void deleteAllContentByProcessInstanceId(@Param("processInstanceId") String var1);

    @Query("select sum(r.contentSize) from RelatedContent r where r.createdBy = :user")
    Long getTotalContentSizeForUser(@Param("user") String var1);
}
