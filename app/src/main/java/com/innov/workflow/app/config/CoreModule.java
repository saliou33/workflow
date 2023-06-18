package com.innov.workflow.app.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@ComponentScan(basePackages = {"com.innov.workflow.core"})
@EntityScan(basePackages = {"com.innov.workflow.core"})
@EnableJpaRepositories(basePackages = {"com.innov.workflow.core"})
public class CoreModule {

}
