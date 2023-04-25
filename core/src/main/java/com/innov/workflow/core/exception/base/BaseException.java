package com.innov.workflow.core.exception.base;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String module;

    private String code;

    private Object[] args;

    private String defaultMessage;

    public BaseException(String module, String code, Object [] args) {
        this(module, code, args, null);
    }

}
