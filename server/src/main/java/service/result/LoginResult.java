package service.result;

import service.request.GeneralApi;

public record LoginResult (String username, String authToken) implements GeneralApi {
}
