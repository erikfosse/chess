package dataaccess.interfaces;

import model.AuthRecord;
import model.exception.DataAccessException;
import model.exception.SQLConnException;

import java.sql.SQLException;

public interface AuthInterface {
    public void addAuth(String username, String authToken) throws SQLException, SQLConnException;
    public AuthRecord getAuth(String username) throws SQLException, DataAccessException;
    public void delAuth(String authToken) throws SQLException, DataAccessException;
    public void deleteData() throws SQLException, DataAccessException;
}
