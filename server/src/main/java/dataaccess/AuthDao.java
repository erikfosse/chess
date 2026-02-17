package dataaccess;

import chess.AuthRecord;
import db.AuthData;

public class AuthDao {
    public static void addAuth(String username, String authToken) {
        AuthData.addAuth(new AuthRecord(username, authToken));
    }
    public static AuthRecord getAuth(String username) {
        var authTokens = AuthData.getAllAuthTokens();
        for (AuthRecord auth : authTokens) {
            if (auth.username().equals(username)) {
                return auth;
            }
        } return null;
    }
}
