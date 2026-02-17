package dataaccess;

import chess.AuthRecord;
import db.UserData;
import chess.UserRecord;

public class UserDao implements UserInterface {

    @Override
    public void addUser(String username, String password, String email) {
        UserData.addUser(new UserRecord(username, password, email));
    }

    @Override
    public UserRecord getUser(String username) {
        return UserData.getUser(username);
    }
}
