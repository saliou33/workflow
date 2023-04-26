package com.innov.workflow.app.config;

import com.innov.workflow.core.domain.entity.User;
import com.innov.workflow.core.domain.repository.UserRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Configuration
@ComponentScan(basePackages = {"com.innov.workflow.core"})
@EntityScan(basePackages = {"com.innov.workflow.core"})
@EnableJpaRepositories(basePackages = {"com.innov.workflow.core"})
public class CoreModule {


}
