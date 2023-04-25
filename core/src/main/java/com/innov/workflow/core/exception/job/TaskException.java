package com.innov.workflow.core.exception.job;

import com.innov.workflow.core.exception.base.BaseException;

public class TaskException extends BaseException {

    private static final long serialVersionUID = 1L;

    public TaskException (String code, Object[] args) {
        super("task", code, args, null);
    }

}
