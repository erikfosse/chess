package model.exception;

public class SQLConnException extends DataAccessException {
    public SQLConnException() {
        super("Error: sql connection error");
    }
}
