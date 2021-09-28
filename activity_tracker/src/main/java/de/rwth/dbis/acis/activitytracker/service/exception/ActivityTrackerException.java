package de.rwth.dbis.acis.activitytracker.service.exception;


public class ActivityTrackerException extends Exception {

    private final ExceptionLocation location;

    private String message;

    private ErrorCode errorCode;

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

    public ExceptionLocation getLocation() {
        return location;
    }
}
