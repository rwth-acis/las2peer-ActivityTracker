package de.rwth.dbis.acis.activitytracker.service.exception;

public enum ErrorCode {
    UNKNOWN("000", "Unknown, unexpected exception occurred"),
    VALIDATION("001", "Constraint validation failed"),
    CANNOTDELETE("002", "The item cannot be deleted"),
    AUTHORIZATION("003", "This user is not authorized to use this method"),
    DB_COMM("004", "Error during communicating to database. Possibly wrong connection parameters"),
    NOT_FOUND("005", "The item was not found"),
    NETWORK_PROBLEM("006", "Error while trying to receive activity content"),
    WRONG_PARAMETER("006", "Wrong parameter given");

    private final String code;
    private final String message;

    public String asCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
