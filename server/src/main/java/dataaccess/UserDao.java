package dataaccess;

import db.AllUserData;
import chess.UserRecord;

public class UserDao {
    public static void addUser(String username, String password, String email) {
        AllUserData.addUser(new UserRecord(username, password, email));
    }
    public static UserRecord getUser(String username) {
        var users = AllUserData.getAllUsers();
        for (UserRecord user : users) {
            if (user.username().equals(username)) {
                return user;
            }
        } return null;
    }
}
