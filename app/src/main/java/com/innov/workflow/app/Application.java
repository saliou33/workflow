
package com.innov.workflow.app;

import com.innov.workflow.idm.service.MyService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication(scanBasePackages = "com.innov.workflow")
@EnableConfigurationProperties
@EntityScan(basePackages = {"com.innov.workflow.core"})
@RestController
public class Application {

  private final MyService myService;

  public Application(MyService myService) {
    this.myService = myService;
  }

  @GetMapping("/")
  public String home() {
    return myService.message();
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}