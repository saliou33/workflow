package com.innov.workflow.idm.config.config;

import com.innov.workflow.core.domain.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
public class ExceptionResolver extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AuthenticationException.class})
    @ResponseBody
    public ResponseEntity ResponseEntityhandleAuthenticationException(Exception ex) {
        ApiResponse response = new ApiResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return response.build();
    }
}
