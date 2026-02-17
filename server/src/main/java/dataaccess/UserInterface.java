package dataaccess;

import chess.UserRecord;

public interface UserInterface {
    public void addUser(String username, String password, String email);
    public UserRecord getUser(String username);
}
