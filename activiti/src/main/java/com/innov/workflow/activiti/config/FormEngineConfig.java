package com.innov.workflow.activiti.config;

import com.innov.workflow.activiti.cache.DeploymentCacheImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.form.api.FormRepositoryService;
import org.activiti.form.api.FormService;
import org.activiti.form.engine.Clock;
import org.activiti.form.engine.FormEngine;
import org.activiti.form.engine.FormEngineConfiguration;

import org.activiti.form.engine.impl.cfg.StandaloneFormEngineConfiguration;
import org.activiti.form.engine.impl.persistence.deploy.DeploymentCache;
import org.activiti.form.engine.impl.persistence.deploy.FormCacheEntry;
import org.activiti.form.engine.impl.persistence.entity.FormDeploymentEntityManager;
import org.activiti.form.engine.impl.persistence.entity.FormEntityManager;

import org.activiti.form.engine.impl.persistence.entity.FormEntityManagerImpl;
import org.activiti.form.engine.impl.persistence.entity.data.FormDataManager;
import org.activiti.form.engine.impl.persistence.entity.data.impl.MybatisFormDataManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FormEngineConfig {


    private final DataSource dataSource;
    private final DeploymentCacheImpl<FormCacheEntry> deploymentCache;
    private static FormEngine formEngine;


    @Bean
    public FormEngineConfiguration formEngineConfiguration() {
        FormEngineConfiguration formEngineConfiguration = new StandaloneFormEngineConfiguration();
        formEngineConfiguration.setDataSource(dataSource);
        FormDataManager formDataManager = new MybatisFormDataManager(formEngineConfiguration);
        FormEntityManager formEntityManager = new FormEntityManagerImpl(formEngineConfiguration, formDataManager);
        formEngineConfiguration.setFormEntityManager(formEntityManager);
        deploymentCache.setFormEntityManager(formEngineConfiguration.getFormEntityManager());
        formEngineConfiguration.setFormCache(deploymentCache.getImpl());
        formEngineConfiguration.setDatabaseSchemaUpdate("true");
        formEngine = formEngineConfiguration.buildFormEngine();
        return formEngineConfiguration;
    }


    @Bean(name = "formEngine")
    public FormEngine formEngine(@Qualifier("formEngineConfiguration") FormEngineConfiguration formEngineConfiguration) {
        return formEngine;
    }
    
//    @Bean
//    public CommandContextFactory commandContextFactory (@Qualifier("formEngineConfiguration") FormEngineConfiguration formEngineConfiguration) {
//        return formEngineConfiguration.getCommandContextFactory();
//    }

//    @Bean(name  = "commandExecutor")
//    public CommandExecutor commandExecutor (@Qualifier("formEngineConfiguration") FormEngineConfiguration formEngineConfiguration) {
//        return formEngineConfiguration.getCommandExecutor();
//    }
 
    @Bean(name = "deploymentCache")
    public DeploymentCache deploymentCache(@Qualifier("formEngineConfiguration") FormEngineConfiguration formEngineConfiguration) {
        return formEngineConfiguration.getFormCache();
    }


    @Bean(name = "formRepositoryService")
    public FormRepositoryService formRepositoryService(
            @Qualifier("formEngine") FormEngine formEngine) {

        return formEngine.getFormRepositoryService();
    }

    @Bean(name = "formEntityManager")
    public FormEntityManager formEntityManager (@Qualifier("formEngineConfiguration") FormEngineConfiguration formEngineConfiguration) {
        return formEngineConfiguration.getFormEntityManager();
    }

    @Bean(name = "formDeploymentEntityManager")
    public FormDeploymentEntityManager formDeploymentEntityManager (@Qualifier("formEngineConfiguration") FormEngineConfiguration formEngineConfiguration) {
        return  formEngineConfiguration.getDeploymentEntityManager();
    }


    @Bean(name = "formService")
    public FormService formService(
            @Qualifier("formEngine") FormEngine formEngine) {

        return formEngine.getFormService();
    }

    public Clock clock(
            @Qualifier("formEngine") FormEngine formEngine) {
        return formEngine.getFormEngineConfiguration().getClock();
    }

}

