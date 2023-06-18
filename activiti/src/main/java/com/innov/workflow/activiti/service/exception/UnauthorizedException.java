package com.innov.workflow.activiti.service.exception;

public class UnauthorizedException extends BaseModelerRestException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }
}
