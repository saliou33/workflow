package com.innov.workflow.activiti.service.exception;

public class ModelErrorException extends BaseModelerRestException {
    private static final long serialVersionUID = 1L;

    public ModelErrorException() {
    }

    public ModelErrorException(String message) {
        super(message);
    }

    public ModelErrorException(String message, Throwable t) {
        super(message, t);
    }
}
