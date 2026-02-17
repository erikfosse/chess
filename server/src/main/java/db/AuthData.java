package db;

import chess.AuthRecord;

import java.util.List;
import java.util.ArrayList;

public class AuthData {

    private static final ArrayList<AuthRecord> authTokens = new ArrayList<>();

    public static void addAuth(AuthRecord auth) {
        authTokens.add(auth);
    }

    public static List<AuthRecord> getAllAuthTokens() {
        return authTokens;
    }
}