package com.innov.workflow.app.config;

import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.exception.ApiException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleValidationErrors(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ApiResponse responseData = new ApiResponse();
        responseData.put("msg", "Erreurs Validation");
        responseData.put("errors", errors);

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity handleApiException(ApiException e) {

        ApiResponse responseData = new ApiResponse();
        responseData.put("msg", e.getMsg());
        responseData.put("code", e.getCode().name());
        if (e.getErrors() != null) {
            responseData.put("errors", e.getErrors());
        }

        return new ResponseEntity<>(responseData, e.getCode());
    }

    @ExceptionHandler({AuthenticationException.class, JwtException.class})
    public ResponseEntity handleCoreException(Exception e) {
        ApiResponse responseData = new ApiResponse();
        responseData.put("msg", e.getMessage());
        responseData.put("code", HttpStatus.BAD_REQUEST.name());
        return new ResponseEntity(responseData, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        e.printStackTrace();
        ApiResponse responseData = new ApiResponse();
        responseData.put("msg", "Server Error");
        responseData.put("code", HttpStatus.INTERNAL_SERVER_ERROR.name());
        return new ResponseEntity(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
