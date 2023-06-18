package com.innov.workflow.activiti.repository.editor;

import com.innov.workflow.activiti.domain.editor.ModelHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelHistoryRepository extends JpaRepository<ModelHistory, String> {
    List<ModelHistory> findByCreatedByAndModelTypeAndRemovalDateIsNull(String var1, Integer var2);

    List<ModelHistory> findByModelIdAndRemovalDateIsNullOrderByVersionDesc(String var1);

    List<ModelHistory> findByModelIdOrderByVersionDesc(Long var1);
}
