package model.exception;

public class IncorrectAuthException extends GeneralException{
    public IncorrectAuthException(String error) {
        super(error);
    }
}