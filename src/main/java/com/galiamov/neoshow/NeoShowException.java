package com.galiamov.neoshow;

public class NeoShowException extends RuntimeException {

    public static final String INVALID_JSON_FORMAT = "invalid_json_format";
    public static final String INVALID_DATE_FORMAT = "invalid_date_format";
    public static final String RESULT_PROCESSING_ERROR = "result_processing_error";
    public static final String INVALID_EMAIL = "invalid_email";
    public static final String INVALID_AGE = "invalid_age";
    public static final String INVALID_GENDER = "invalid_gender";
    public static final String INVALID_TITLE = "invalid_title";
    public static final String INVALID_RELEASE_DATE = "invalid_release_date";
    public static final String USER_NOT_FOUND = "user_not_found";
    public static final String TVSHOW_NOT_FOUND = "tvshow_not_found";

    final String reason;

    public NeoShowException(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
