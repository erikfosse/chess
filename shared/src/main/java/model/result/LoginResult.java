package model.result;

import model.request.GeneralApi;

public record LoginResult (String username, String authToken) implements GeneralApi {
}
