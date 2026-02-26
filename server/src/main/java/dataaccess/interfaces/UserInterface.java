package dataaccess.interfaces;

import model.UserRecord;

public interface UserInterface {
    public void addUser(String username, String password, String email);
    public UserRecord getUser(String username);
}
