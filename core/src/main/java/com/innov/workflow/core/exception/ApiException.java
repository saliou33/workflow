package com.innov.workflow.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiException extends RuntimeException {
    private String msg;
    private HttpStatus code;
    private List<Object> errors;


    public ApiException(HttpStatus code, String msg) {
        this.msg = msg;
        this.code = code;
    }

}
