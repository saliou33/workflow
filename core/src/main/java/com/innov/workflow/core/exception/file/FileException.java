package com.innov.workflow.core.exception.file;

import com.innov.workflow.core.exception.base.BaseException;

public class FileException extends BaseException {

    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args) {
        super("file", code, args, null);
    }

}
