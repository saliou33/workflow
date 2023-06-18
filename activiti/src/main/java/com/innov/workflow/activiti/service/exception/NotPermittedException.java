package com.innov.workflow.activiti.service.exception;

public class NotPermittedException extends BaseModelerRestException {
    private static final long serialVersionUID = 1L;

    public NotPermittedException() {
    }

    public NotPermittedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotPermittedException(String message) {
        super(message);
    }

    public NotPermittedException(Throwable cause) {
        super(cause);
    }
}
