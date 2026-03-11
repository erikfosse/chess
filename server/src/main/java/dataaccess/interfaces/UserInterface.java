package dataaccess.interfaces;

import model.UserRecord;

import java.sql.SQLException;

public interface UserInterface {
    public void addUser(String username, String password, String email) throws SQLException;
    public UserRecord getUser(String username) throws SQLException;
    public void deleteData() throws SQLException;
}
