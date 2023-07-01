package com.innov.workflow.activiti.custom.dao;

import com.innov.workflow.activiti.custom.form.ActFormDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class ActFormDefinitionService {
    @Autowired
    private DataSource dataSource;

    public void save(ActFormDefinition formDefinition) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO ACT_FO_FORM_DEFINITION (ID_, NAME_, VERSION_, KEY_, DEPLOYMENT_ID_, " +
                    "TENANT_ID_, RESOURCE_NAME_, DESCRIPTION_, PARENT_DEPLOYMENT_ID_) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, formDefinition.getId());
            statement.setString(2, formDefinition.getName());
            statement.setInt(3, formDefinition.getVersion());
            statement.setString(4, formDefinition.getKey());
            statement.setString(5, formDefinition.getDeploymentId());
            statement.setString(6, formDefinition.getTenantId());
            statement.setString(7, formDefinition.getResourceName());
            statement.setString(8, formDefinition.getDescription());
            statement.setString(9, formDefinition.getParentDeploymentId());

            statement.executeUpdate();
        } catch (SQLException e) {
            // Handle any potential exceptions
        }
    }
}