package db;

import chess.UserRecord;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class UserData {
    private static final Map<String, UserRecord> users = Map.of();
    public static void addUser(UserRecord user) {
        users.put(user.username(), user);
    }
    public static UserRecord getUser(String username) {
        return users.get(username);
    }
}
