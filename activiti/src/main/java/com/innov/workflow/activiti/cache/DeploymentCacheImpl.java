package com.innov.workflow.activiti.cache;

import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.repository.editor.ModelRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.activiti.form.api.Form;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.engine.impl.persistence.deploy.DefaultDeploymentCache;
import org.activiti.form.engine.impl.persistence.deploy.DeploymentCache;
import org.activiti.form.engine.impl.persistence.deploy.FormCacheEntry;

import org.activiti.form.engine.impl.persistence.entity.FormEntity;
import org.activiti.form.engine.impl.persistence.entity.FormEntityManager;
import org.activiti.form.engine.impl.persistence.entity.FormEntityManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
public class DeploymentCacheImpl<T> {

    private final ModelRepository modelRepository;
    protected FormEntityManager formEntityManager;

    class Impl<T> extends DefaultDeploymentCache<T> {
        Impl (int limit) {
            super(limit);
        }

        @Override
        public T get(String id) {
            if(super.get(id) == null) {
                cache(id);
            }
            return super.get(id);
        }

        private void cache(String id) {
            FormEntity formEntity = formEntityManager.findById(id);
            Model model = modelRepository.findModelsByKeyAndType(formEntity.getKey(), 2).get(0);
            super.add(id, (T) new FormCacheEntry(formEntity, model.getModelEditorJson()));
        }
    }

     public DeploymentCache getImpl() {
        return  new Impl(1000);
    }
}
