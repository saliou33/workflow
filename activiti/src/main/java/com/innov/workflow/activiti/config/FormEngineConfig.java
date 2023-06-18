package com.innov.workflow.activiti.config;

import lombok.RequiredArgsConstructor;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.api.FormService;
import org.activiti.form.engine.Clock;
import org.activiti.form.engine.FormEngine;
import org.activiti.form.engine.impl.cfg.StandaloneFormEngineConfiguration;
import org.activiti.form.engine.impl.persistence.deploy.DefaultDeploymentCache;
import org.activiti.form.engine.impl.persistence.deploy.DeploymentCache;
import org.activiti.form.engine.impl.persistence.deploy.FormCacheEntry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class FormEngineConfig {


    private final DataSource dataSource;
    private PlatformTransactionManager transactionManager;
    private Environment environment;


    @Bean
    public StandaloneFormEngineConfiguration formEngineConfiguration() {
        StandaloneFormEngineConfiguration formEngineConfiguration = new StandaloneFormEngineConfiguration();
        formEngineConfiguration.setDataSource(dataSource);
        formEngineConfiguration.setDatabaseSchemaUpdate("true");
        DeploymentCache<FormCacheEntry> formCache = new DefaultDeploymentCache<>(1000);
        formEngineConfiguration.setFormCache(formCache);

        return formEngineConfiguration;
    }


    @Bean(name = "formEngine")
    public FormEngine formEngine(@Qualifier("formEngineConfiguration") StandaloneFormEngineConfiguration formEngineConfiguration) {

        return formEngineConfiguration.buildFormEngine();
    }

    @Bean(name = "deploymentCache")
    public DeploymentCache deploymentCache(@Qualifier("formEngineConfiguration") StandaloneFormEngineConfiguration formEngineConfiguration) {
        return formEngineConfiguration.getFormCache();
    }


    @Bean(name = "formRepositoryService")
    public FormRepositoryService formRepositoryService(
            @Qualifier("formEngine") FormEngine formEngine) {

        return formEngine.getFormRepositoryService();
    }


    @Bean(name = "formService")
    public FormService formService(
            @Qualifier("formEngine") FormEngine formEngine) {

        return formEngine.getFormService();
    }

    @Bean
    public Clock clock(
            @Qualifier("formEngine") FormEngine formEngine) {

        return formEngine.getFormEngineConfiguration().getClock();
    }


}

