package model.exception;

import model.request.GeneralApi;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception implements GeneralApi {
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }
}
