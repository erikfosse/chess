package db;

import chess.AuthRecord;
import chess.UserRecord;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class AuthData {
    private static final Map<String, AuthRecord> authTokens = new HashMap<>();
    public static void addAuth(AuthRecord auth) {
        authTokens.put(auth.username(), auth);
    }
    public static AuthRecord getAuth(String username) {
        return authTokens.get(username);
    }
}