package dataaccess.memory;

import dataaccess.interfaces.UserInterface;
import db.UserData;
import model.UserRecord;

public class MemoryUserDao implements UserInterface {

    @Override
    public void addUser(String username, String password, String email) {
        UserData.addUser(new UserRecord(username, password, email));
    }

    @Override
    public UserRecord getUser(String username) {
        return UserData.getUser(username);
    }

    public void deleteData() {
        UserData.deleteData();
    }
}
