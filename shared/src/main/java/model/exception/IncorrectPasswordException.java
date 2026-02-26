package model.exception;

public class IncorrectPasswordException extends GeneralException{
    public IncorrectPasswordException(String error) {
        super(error);
    }
}
