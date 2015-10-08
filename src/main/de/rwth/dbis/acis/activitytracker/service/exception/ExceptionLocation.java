package de.rwth.dbis.acis.activitytracker.service.exception;

public enum ExceptionLocation {
    REPOSITORY("01", "Repository"),
    TRANSFORMATOR("02", "Transformator"),
    DALFACADE("03", "DAL facade implementation"),
    BAZAARSERVICE("04", "ActivityTracker service");

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
