package model.result;

import model.request.GeneralApi;

public record RegisterResult (String username, String authToken) implements GeneralApi {}
