package de.rwth.dbis.acis.activitytracker.service.exception;

public enum ExceptionLocation {
    REPOSITORY("01", "Repository"),
    TRANSFORMATOR("02", "Transformator"),
    DALFACADE("03", "DAL facade implementation"),
    ACTIVITIESERVICE("04", "ActivityTracker service"),
    NETWORK("5", "Network");

    private final String code;
    private final String message;

    public String asCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ExceptionLocation(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
