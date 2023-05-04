package com.innov.workflow.app.controller.test;

import com.innov.workflow.core.domain.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/")
    public ResponseEntity getServer() {
        return ApiResponse.success("Workflow BackEnd Server");
    }

}
