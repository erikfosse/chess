package db;

import chess.AuthRecord;
import chess.UserRecord;

import java.util.List;
import java.util.ArrayList;

public class AllUserData {
    private static final ArrayList<UserRecord> users = new ArrayList<>();
    private static final ArrayList<AuthRecord> authTokens = new ArrayList<>();

    public static void addUser(UserRecord user) {
        users.add(user);
    }

    public static List<UserRecord> getAllUsers() {
        return users;
    }

    public static void addAuth(AuthRecord auth) {
        authTokens.add(auth);
    }

    public static List<AuthRecord> getAllAuthTokens() {
        return authTokens;
    }
}
