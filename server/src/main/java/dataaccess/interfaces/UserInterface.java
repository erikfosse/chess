package dataaccess.interfaces;

import model.UserRecord;
import model.exception.DataAccessException;
import model.exception.SQLConnException;

import java.sql.SQLException;

public interface UserInterface {
    public void addUser(String username, String password, String email) throws SQLException, DataAccessException;
    public UserRecord getUser(String username) throws SQLException, DataAccessException;
    public void deleteData() throws SQLException, DataAccessException;
}
