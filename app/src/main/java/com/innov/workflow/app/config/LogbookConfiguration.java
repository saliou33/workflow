package com.innov.workflow.app.config;//
//import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
//import org.zalando.logbook.Logbook;
//
//import static org.zalando.logbook.core.Conditions.exclude;
//import static org.zalando.logbook.core.Conditions.requestTo;


@Configuration
public class LogbookConfiguration {

//    @Bean
//    public Logbook logbook () {
//        Logbook logbook = Logbook.builder()
//                .condition(
//                        exclude(
//                            requestTo("/app/rest/authenticate/**"),
//                            requestTo( "/app/rest/account/**")
//                        )
//                )
//                .build();
//        return  logbook;
//    }
}
