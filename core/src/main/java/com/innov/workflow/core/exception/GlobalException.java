package com.innov.workflow.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GlobalException extends RuntimeException {

    public static final long serialVersionUID = 1L;

    private String message;

    private String detailMessage;

    public GlobalException(String message) {
        this.message = message;
    }

}
