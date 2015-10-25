package de.rwth.dbis.acis.activitytracker.service.exception;

import com.google.gson.annotations.Expose;

public class ActivityTrackerException extends Exception {
    @Expose
    private String message;
    @Expose
    private ErrorCode errorCode;
    @Expose
    private final ExceptionLocation location;

    protected ActivityTrackerException(ExceptionLocation location) {
        this.location = location;
        message = new String();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public int getExceptionCode() {
        return Integer.valueOf(location.asCode() + errorCode.asCode());
    }
}
