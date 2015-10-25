package de.rwth.dbis.acis.activitytracker.service.exception;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public enum ExceptionHandler {
    INSTANCE;

    // Static getter
    public static ExceptionHandler getInstance() {
        return INSTANCE;
    }

    public void throwException(ExceptionLocation location, ErrorCode errorCode, String message) throws ActivityTrackerException {
        ActivityTrackerException activityTrackerException = new ActivityTrackerException(location);
        activityTrackerException.setErrorCode(errorCode);
        activityTrackerException.setMessage(message);
        throw activityTrackerException;
    }

    public ActivityTrackerException convert(Exception ex, ExceptionLocation location, ErrorCode errorCode, String message) {
        ActivityTrackerException activityTrackerException = new ActivityTrackerException(location);
        activityTrackerException.setErrorCode(errorCode);
        activityTrackerException.setMessage(ex.getMessage() + message);
        return activityTrackerException;
    }

    public void convertAndThrowException(Exception exception, ExceptionLocation location, ErrorCode errorCode) throws ActivityTrackerException {
        throw convert(exception, location, errorCode, "");
    }

    public void convertAndThrowException(Exception exception, ExceptionLocation location, ErrorCode errorCode, String message) throws ActivityTrackerException {
        throw convert(exception, location, errorCode, message);
    }

    public void convertAndThrowException(ActivityTrackerException activityEx) throws ActivityTrackerException {
        throw activityEx;
    }

    public String toJSON(ActivityTrackerException exception) {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        final Gson gson = builder.create();
        return gson.toJson(exception);
    }
}
