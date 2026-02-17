package service.result;

import service.request.GeneralApi;

public record RegisterResult (String username, String authToken) implements GeneralApi {}
