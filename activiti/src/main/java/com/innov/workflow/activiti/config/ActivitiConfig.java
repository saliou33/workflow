package com.innov.workflow.activiti.config;


import com.innov.workflow.activiti.event.CustomActivitiEventListener;
import lombok.RequiredArgsConstructor;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.util.DefaultClockImpl;
import org.activiti.engine.runtime.Clock;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


@Configuration
@RequiredArgsConstructor
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer {

    private final CustomActivitiEventListener customActivitiEventListener;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String jdbcUsername;

    @Value("${spring.datasource.password}")
    private String jdbcPassword;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private Integer mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${spring.mail.protocol")
    private String mailProtocol;

    private static  SpringProcessEngineConfiguration configuration = null;


    @Override
    public void configure(SpringProcessEngineConfiguration springProcessEngineConfiguration) {
        springProcessEngineConfiguration
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                .setJdbcUrl(jdbcUrl)
                .setJdbcUsername(jdbcUsername)
                .setJdbcPassword(jdbcPassword)
                .setMailServerHost(mailHost)
                .setMailServerUsername(mailUsername)
                .setMailServerPassword(mailPassword)
                .setMailServerPort(mailPort)
                .setAsyncExecutorActivate(true)
                .setClock(new DefaultClockImpl());


        springProcessEngineConfiguration.setEventListeners(Arrays.asList(new ActivitiEventListener[]{
                customActivitiEventListener
        }));

        configuration = springProcessEngineConfiguration;
    }

    @Bean
    Clock getClock () {
        return  configuration.getClock();
    }
}