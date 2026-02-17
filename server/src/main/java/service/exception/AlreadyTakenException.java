package service.exception;

import service.request.GeneralApi;

public record AlreadyTakenException(String message) implements GeneralApi {}
