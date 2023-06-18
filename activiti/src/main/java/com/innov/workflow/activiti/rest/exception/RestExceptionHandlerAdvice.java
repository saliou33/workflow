package com.innov.workflow.activiti.rest.exception;

import com.innov.workflow.activiti.old.service.IdentityService;
import com.innov.workflow.activiti.service.exception.*;
import com.innov.workflow.core.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class RestExceptionHandlerAdvice {
    private static final String UNAUTHORIZED_MESSAGE_KEY = "GENERAL.ERROR.UNAUTHORIZED";
    private static final String NOT_FOUND_MESSAGE_KEY = "GENERAL.ERROR.NOT-FOUND";
    private static final String BAD_REQUEST_MESSAGE_KEY = "GENERAL.ERROR.BAD-REQUEST";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE_KEY = "GENERAL.ERROR.INTERNAL-SERVER_ERROR";
    private static final String FORBIDDEN_MESSAGE_KEY = "GENERAL.ERROR.FORBIDDEN";
    private static final String INACTIVE_USER_MESSAGE_KEY = "GENERAL.ERROR.INACTIVE_USER";
    private static final String UPLOAD_LIMIT_EXCEEDED = "GENERAL.ERROR.UPLOAD-LIMIT-EXCEEDED";
    private static final String UPLOAD_LIMIT_EXCEEDED_TRIAL_USER = "GENERAL.ERROR.UPLOAD-LIMIT-EXCEEDED-TRIAL-USER";
    private static final String QUOTA_EXCEEDED_PREFIX = "GENERAL.ERROR.QUOTA-EXCEEDED-";

    @Autowired
    IdentityService identityService;

    public RestExceptionHandlerAdvice() {
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    @ResponseBody
    public ErrorInfo handleNotFound(NotFoundException e) {
        return this.createInfoFromException(e, "GENERAL.ERROR.NOT-FOUND");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NonJsonResourceNotFoundException.class})
    public void handleNonJsonResourceNotFound(NonJsonResourceNotFoundException e) {
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class})
    @ResponseBody
    public ErrorInfo handleBadRequest(BadRequestException e) {
        return this.createInfoFromException(e, "GENERAL.ERROR.BAD-REQUEST");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({InternalServerErrorException.class})
    @ResponseBody
    public ErrorInfo handleInternalServerError(InternalServerErrorException e) {
        return this.createInfoFromException(e, "GENERAL.ERROR.INTERNAL-SERVER_ERROR");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({NotPermittedException.class})
    @ResponseBody
    public ErrorInfo handleNoPermission(NotPermittedException e) {
        return this.createInfoFromException(e, "GENERAL.ERROR.FORBIDDEN");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({LockedException.class})
    @ResponseBody
    public ErrorInfo handleLockedUser(LockedException e) {
        ErrorInfo result = new ErrorInfo(e.getMessage());
        result.setMessageKey("GENERAL.ERROR.INACTIVE_USER");
        return result;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({UnauthorizedException.class})
    @ResponseBody
    public ErrorInfo handleUnauthorized(UnauthorizedException e) {
        return this.createInfoFromException(e, "GENERAL.ERROR.UNAUTHORIZED");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({ConflictingRequestException.class})
    @ResponseBody
    public ErrorInfo handleConflict(ConflictingRequestException e) {
        return this.createInfoFromException(e, "GENERAL.ERROR.BAD-REQUEST");
    }

    @ResponseStatus(HttpStatus.REQUEST_ENTITY_TOO_LARGE)
    @ExceptionHandler({MaxUploadSizeExceededException.class})
    @ResponseBody
    public ErrorInfo handleMaxFileSizeExceeded(MaxUploadSizeExceededException musee) {
        ErrorInfo errorInfo = new ErrorInfo("Maximum upload size exceeded");
        User currentUser = identityService.getCurrentUserObject();
        errorInfo.setMessageKey("GENERAL.ERROR.UPLOAD-LIMIT-EXCEEDED-TRIAL-USER");
        errorInfo.addParameter("quota", musee.getMaxUploadSize());
        return errorInfo;
    }

    protected ErrorInfo createInfoFromException(BaseModelerRestException exception, String defaultMessageKey) {
        ErrorInfo result = null;
        result = new ErrorInfo(exception.getMessage());
        if (exception.getCustomData() != null) {
            result.setCustomData(exception.getCustomData());
        }

        if (exception.getMessageKey() != null) {
            result.setMessageKey(exception.getMessageKey());
        } else {
            result.setMessageKey(defaultMessageKey);
        }

        return result;
    }

    protected String getSafeMessageKey(String fragment) {
        return fragment != null ? fragment.toUpperCase() : "";
    }
}
