package model.exception;

import model.request.GeneralApi;

public record ErrorResult(String message) implements GeneralApi {
}
