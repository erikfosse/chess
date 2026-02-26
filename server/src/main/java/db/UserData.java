package db;

import model.UserRecord;

import java.util.HashMap;
import java.util.Map;

public class UserData {
    private static Map<String, UserRecord> users = new HashMap<>();
    public static void addUser(UserRecord user) {
        users.put(user.username(), user);
    }
    public static UserRecord getUser(String username) {
        return users.get(username);
    }
    public static void deleteData() {
        users = new HashMap<>();
    }
}
