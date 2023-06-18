package com.innov.workflow.app.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@ComponentScan(basePackages = {"com.innov.workflow.activiti"})
@EntityScan(basePackages = {"com.innov.workflow.activiti"})
@EnableJpaRepositories(basePackages = {"com.innov.workflow.activiti"})
public class ActivitiModule {
}
