package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.interfaces.AuthInterface;
import db.AuthData;
import model.AuthRecord;
import model.exception.DataAccessException;
import model.exception.SQLConnException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLAuthDao implements AuthInterface {

    public SQLAuthDao() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
    }

    @Override
    public void addAuth(String username, String authToken) throws SQLConnException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "INSERT INTO authData (username, authToken) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new SQLConnException();
        }
    }

    @Override
    public AuthRecord getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
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
        } catch (SQLException e) {
            throw new SQLConnException();
        }
    }

    @Override
    public void delAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM authData WHERE authToken=?")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLConnException();
        }
    }

    @Override
    public void deleteData() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE authData")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLConnException();
        }
    }
}
