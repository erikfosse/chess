package service;

public record RegisterResult (String username, String authToken) implements GeneralApi {}
