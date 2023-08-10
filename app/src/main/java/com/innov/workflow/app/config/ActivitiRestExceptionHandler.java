package com.innov.workflow.app.config;

import com.innov.workflow.activiti.rest.exception.RestExceptionHandlerAdvice;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ActivitiRestExceptionHandler extends RestExceptionHandlerAdvice {
}
