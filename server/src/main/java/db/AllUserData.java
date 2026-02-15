package db;

import chess.UserRecord;

import java.util.List;
import java.util.ArrayList;

public class AllUserData {
    private static final ArrayList<UserRecord> users = new ArrayList<>();

    public static void addUser(UserRecord user) {
        users.add(user);
    }

    public static List<UserRecord> getAllUsers() {
        return users;
    }
}
