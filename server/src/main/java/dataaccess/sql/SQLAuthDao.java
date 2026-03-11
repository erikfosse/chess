package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.interfaces.AuthInterface;
import db.AuthData;
import model.AuthRecord;
import model.exception.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLAuthDao implements AuthInterface {

    private Connection conn;

    public SQLAuthDao() throws DataAccessException, SQLException {
        this.conn = DatabaseManager.getConnection();
    }

    @Override
    public void addAuth(String username, String authToken) throws SQLException {
        try (var preparedStatement = conn.prepareStatement(
                "INSERT INTO authData (username, authToken) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, authToken);
        }
    }

    @Override
    public AuthRecord getAuth(String username) throws SQLException {
        try (var preparedStatement = conn.prepareStatement(
                "SELECT username, authToken FROM authData WHERE username=?")) {
            preparedStatement.setString(1, username);
            try (var rs = preparedStatement.executeQuery()) {
                String authToken = "";
                while (rs.next()) {
                    authToken = rs.getString("authToken");
                }
                return new AuthRecord(username, authToken);
            }
        }
    }

    @Override
    public void delAuth(String authToken) throws SQLException {
        try (var preparedStatement = conn.prepareStatement("DELETE FROM authData WHERE authToken=?")) {
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteData() throws SQLException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE authData")) {
            preparedStatement.executeUpdate();
        }
    }
}
