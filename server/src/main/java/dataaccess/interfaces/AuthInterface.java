package dataaccess.interfaces;

import model.AuthRecord;

import java.sql.SQLException;

public interface AuthInterface {
    public void addAuth(String username, String authToken) throws SQLException;
    public AuthRecord getAuth(String username) throws SQLException;
    public void delAuth(String authToken) throws SQLException;
    public void deleteData() throws SQLException;
}
