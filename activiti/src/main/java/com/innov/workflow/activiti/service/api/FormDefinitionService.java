package com.innov.workflow.activiti.service.api;

import com.innov.workflow.activiti.domain.editor.Model;
import com.innov.workflow.activiti.repository.editor.ModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.engine.impl.persistence.deploy.DeploymentCache;
import org.activiti.form.engine.impl.persistence.deploy.FormCacheEntry;
import org.activiti.form.engine.impl.persistence.entity.FormEntity;
import org.activiti.form.engine.impl.persistence.entity.FormEntityImpl;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class FormDefinitionService {

    private final DataSource dataSource;
    private final FormRepositoryService formRepositoryService;
    private final DeploymentCache<FormCacheEntry> deploymentCache;
    private final ModelRepository modelRepository;

    public void insertFormDefinition(String formKey, String resourceName, String deploymentId, String name,
                                     String tenantId, int version, String parentDeploymentId, String description) {
        try (Connection connection = dataSource.getConnection()) {
            String formDefinitionId = UUID.randomUUID().toString();

            String sql = "INSERT INTO ACT_FO_FORM_DEFINITION (ID_, VERSION_, KEY_, DEPLOYMENT_ID_," +
                    " TENANT_ID_, RESOURCE_NAME_, PARENT_DEPLOYMENT_ID_, DESCRIPTION_) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, formDefinitionId); // ID_
                statement.setInt(2, version); // VERSION_
                statement.setString(3, formKey); // KEY_
                statement.setString(4, deploymentId); // DEPLOYMENT_ID_
                statement.setString(5, tenantId); // TENANT_ID_
                statement.setString(6, resourceName); // RESOURCE_NAME_
                statement.setString(7, parentDeploymentId); // PARENT_DEPLOYMENT_ID_
                statement.setString(8, description); // DESCRIPTION_

                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    log.info("Form definition inserted successfully.");
                } else {
                    log.error("Failed to insert form definition.");
                }
            }

            cache(formDefinitionId, formKey, resourceName, deploymentId, name, tenantId, version, parentDeploymentId, description);

        } catch (SQLException e) {
            log.error("Error inserting form definition: " + e.getMessage(), e);
        }
    }

    private void cache(String id, String formKey, String resourceName, String deploymentId, String name,
                       String tenantId, int version, String parentDeploymentId, String description) {


        FormEntity formEntity = new FormEntityImpl();
        formEntity.setKey(formKey);
        formEntity.setId(id);
        formEntity.setName(name);
        formEntity.setDeploymentId(deploymentId);
        formEntity.setResourceName(resourceName);
        formEntity.setParentDeploymentId(parentDeploymentId);
        formEntity.setTenantId(tenantId);
        formEntity.setVersion(version);
        formEntity.setDescription(description);

        Model model = modelRepository.findModelsByKeyAndType(formKey, 2).get(0);

        FormCacheEntry cacheEntry = new FormCacheEntry(formEntity, model.getModelEditorJson());
        System.out.println(deploymentCache);
        deploymentCache.add(id, cacheEntry);
        System.out.println(deploymentCache);
    }


}