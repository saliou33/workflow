package com.innov.workflow.app.config;

import com.innov.workflow.core.domain.ApiResponse;
import com.innov.workflow.core.exception.ApiException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
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
        } else {
            List<String> errors = new ArrayList<String>();
            errors.add(e.getMsg());
            responseData.put("errors", errors);
        }

        return new ResponseEntity<>(responseData, e.getCode());
    }

}
