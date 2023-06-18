package com.innov.workflow.activiti.repository.editor;

import com.innov.workflow.activiti.domain.editor.ModelInformation;
import com.innov.workflow.activiti.domain.editor.ModelRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRelationRepository extends JpaRepository<ModelRelation, Long> {
    @Query("from ModelRelation mr where mr.parentModelId = :parentModelId")
    List<ModelRelation> findByParentModelId(@Param("parentModelId") String var1);

    @Query("from ModelRelation mr where mr.parentModelId = :parentModelId and mr.type = :type")
    List<ModelRelation> findByParentModelIdAndType(@Param("parentModelId") String var1, @Param("type") String var2);

    @Query("from ModelRelation mr where mr.modelId = :modelId")
    List<ModelRelation> findByChildModelId(@Param("modelId") String var1);

    @Query("from ModelRelation mr where mr.modelId = :modelId and mr.type = :type")
    List<ModelRelation> findByChildModelIdAndType(@Param("modelId") String var1, @Param("type") String var2);

    @Query("select m.id, m.name, m.modelType from ModelRelation mr inner join mr.model m where mr.parentModelId = :parentModelId")
    List<ModelInformation> findModelInformationByParentModelId(@Param("parentModelId") String var1);

    @Query("select m.id, m.name, m.modelType from ModelRelation mr inner join mr.parentModel m where mr.modelId = :modelId")
    List<ModelInformation> findModelInformationByChildModelId(@Param("modelId") String var1);

    @Modifying
    @Query("delete from ModelRelation mr where mr.parentModelId = :parentModelId")
    void deleteModelRelationsForParentModel(@Param("parentModelId") String var1);
}
