package model.exception;

import model.request.GeneralApi;

public class GeneralException implements GeneralApi {
    private final String error;
    public GeneralException(String error) {
        this.error = error;
    }
}
