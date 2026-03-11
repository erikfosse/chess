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
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public AuthRecord getAuth(String authToken) throws SQLException {
        try (var preparedStatement = conn.prepareStatement(
                "SELECT username FROM authData WHERE authToken=?"
        )) {
            preparedStatement.setString(1, authToken);
            try (var rs = preparedStatement.executeQuery()) {
                String username = "";
                while (rs.next()) {
                    username = rs.getString("username");
                }
                if (username.isEmpty()) {
                    return null;
                }
                else {
                    return new AuthRecord(username, authToken);
                }
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
