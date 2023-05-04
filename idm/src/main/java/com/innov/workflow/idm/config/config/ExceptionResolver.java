package com.innov.workflow.idm.config.config;

import com.innov.workflow.core.domain.ApiResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ExceptionResolver extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AuthenticationException.class})
    @ResponseBody
    public ResponseEntity handleAuthenticationException(Exception ex) {
        ApiResponse response = new ApiResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return response.build();
    }

    @ExceptionHandler({JwtException.class})
    @ResponseBody
    ResponseEntity handleJwtException(Exception ex) {
        ApiResponse response = new ApiResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return response.build();
    }
}
