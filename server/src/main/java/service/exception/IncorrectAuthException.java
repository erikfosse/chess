package service.exception;

public class IncorrectAuthException extends GeneralException{
    public IncorrectAuthException(String error) {
        super(error);
    }
}