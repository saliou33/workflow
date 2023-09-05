package com.innov.workflow.activiti.repository.editor;

import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.dto.ModelCount;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;


@Repository
public interface ModelRepository extends JpaRepository<Model, String> {


    @Query("from Model as model where model.createdBy = :user and model.modelType = :modelType")
    List<Model> findModelsCreatedBy(@Param("user") String var1, @Param("modelType") Integer var2, Sort var3);

    @Query("from Model as model where model.createdBy = :user and (lower(model.name) like :filter or lower(model.description) like :filter) and model.modelType = :modelType")
    List<Model> findModelsCreatedBy(@Param("user") String var1, @Param("modelType") Integer var2, @Param("filter") String var3, Sort var4);

    @Query("from Model as model where model.key = :key and model.modelType = :modelType")
    List<Model> findModelsByKeyAndType(@Param("key") String var1, @Param("modelType") Integer var2);

    @Query("from Model as model where (lower(model.name) like :filter or lower(model.description) like :filter) and model.modelType = :modelType")
    List<Model> findModelsByModelType(@Param("modelType") Integer var1, @Param("filter") String var2);

    @Query("from Model as model where model.modelType = :modelType")
    List<Model> findModelsByModelType(@Param("modelType") Integer var1);

    @Query("select count(m.id) from Model m where m.createdBy = :user and m.modelType = :modelType")
    Long countByModelTypeAndUser(@Param("modelType") int var1, @Param("user") String var2);

    @Query("select m from ModelRelation mr inner join mr.model m where mr.parentModelId = :parentModelId")
    List<Model> findModelsByParentModelId(@Param("parentModelId") String var1);

    @Query("select m from ModelRelation mr inner join mr.model m where mr.parentModelId = :parentModelId and m.modelType = :modelType")
    List<Model> findModelsByParentModelIdAndType(@Param("parentModelId") String var1, @Param("modelType") Integer var2);

    @Query("select m.id, m.name, m.modelType from ModelRelation mr inner join mr.parentModel m where mr.modelId = :modelId")
    List<Model> findModelsByChildModelId(@Param("modelId") String var1);

    @Query("select model.key from Model as model where model.id = :modelId and model.createdBy = :user")
    String appDefinitionIdByModelAndUser(@Param("modelId") String var1, @Param("user") String var2);

    @Query("select model.modelType as modelType, count(model) as modelCount from Model as model where model.createdBy =:user group by model.modelType")
    List<ModelCount> countModelsByType(@Param("user") String userId);
}
